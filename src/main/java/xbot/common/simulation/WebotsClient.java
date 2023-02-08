package xbot.common.simulation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.wpi.first.wpilibj.util.Color;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;
import xbot.common.subsystems.pose.BasePoseSubsystem;

@Singleton
public class WebotsClient {
    final DoubleProperty simulatorPoseX;
    final DoubleProperty simulatorPoseY;
    final DoubleProperty simulatorPoseYaw;
    final StringProperty simulatorRobotTemplate;
    final BooleanProperty enableProxy;
    final DoubleProperty proxyPort;
    final String hostname = "127.0.0.1";
    final int supervisorPort = 10001;
    final HttpClient client;

    int robotPort = -1;

    private FieldPose fieldOffset;

    @Inject
    public WebotsClient(PropertyFactory propertyFactory) {
        propertyFactory.setPrefix("Webots");
        simulatorPoseX = propertyFactory.createEphemeralProperty("Simulator Pose X", 0);
        simulatorPoseY = propertyFactory.createEphemeralProperty("Simulator Pose Y", 0);
        simulatorPoseYaw = propertyFactory.createEphemeralProperty("Simulator Pose Yaw", 0);
        simulatorRobotTemplate = propertyFactory.createPersistentProperty("Robot Template", "HttpRobotTemplate");
        enableProxy = propertyFactory.createPersistentProperty("Enable Proxy", false);
        proxyPort = propertyFactory.createPersistentProperty("Proxy Port", 8888);

        fieldOffset = new FieldPose();
        client = buildHttpClient(enableProxy.get(), (int)proxyPort.get());
    }

    /**
     * In the case where the origin of the field doesn't match our traditional convention (with 0,0
     * on the bottom-left vertex of the rectangular FRC field if viewed from above with your alliance
     * driver station at the bottom), then we need to shift X,Y coordinates to get everything to line up.
     * @param offset A FieldPose that represents the bottom-left part of the field in Webots (in absolute inches)
     */
    public void setFieldPoseOffset(FieldPose offset) {
        this.fieldOffset = offset;
    }

    public void initialize() {
        // Spawn a robot in the sim
        JSONObject data = new JSONObject();
        data.put("template", simulatorRobotTemplate.get());

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + supervisorPort + "/robot"))
                .header("Content-Type", "application/json").POST(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            robotPort = Integer.parseInt(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JSONObject sendMotors(List<JSONObject> motorValues) {
        JSONObject data = new JSONObject();
        data.put("motors", motorValues);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + robotPort + "/motors"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // parse response for sensor values
                JSONObject responseData = new JSONObject(response.body());
                handleSimulatorPose(responseData.getJSONObject("WorldPose"));
                return responseData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handleSimulatorPose(JSONObject worldPose) {
        JSONArray positionArray = worldPose.getJSONArray("Position");
        FieldPose truePose = new FieldPose(
            positionArray.getDouble(0)*BasePoseSubsystem.INCHES_IN_A_METER, 
            positionArray.getDouble(1)*BasePoseSubsystem.INCHES_IN_A_METER, 
            worldPose.getDouble("Yaw"));
        FieldPose calibratedPose = truePose.getFieldPoseOffsetBy(fieldOffset);
        simulatorPoseX.set(calibratedPose.getPoint().x);
        simulatorPoseY.set(calibratedPose.getPoint().y);
        simulatorPoseYaw.set(calibratedPose.getHeading().getDegrees());
    }

    public void resetPosition() {
        resetPosition(0, 0, 0);
    }

    public void resetPosition(double x, double y, double rotationInDegrees) {
        
        x += fieldOffset.getPoint().x;
        y += fieldOffset.getPoint().y;
        rotationInDegrees += fieldOffset.getHeading().getDegrees();
        
        x /= BasePoseSubsystem.INCHES_IN_A_METER;
        y /= BasePoseSubsystem.INCHES_IN_A_METER;

        JSONArray positionArray = new JSONArray(new double[] {x, y, 0.1});
        JSONArray rotationArray = new JSONArray(new double[] {0, 0, 1, Math.toRadians(rotationInDegrees)});

        JSONObject data = new JSONObject();
        data.put("position", positionArray);
        data.put("rotation", rotationArray);
        // TODO: Support passing in position and or rotation here
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + robotPort + "/position"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // ok
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void drawLine(String name, XYPair point1, XYPair point2, Color color, float zIndex) {
        JSONObject data = new JSONObject();
        data.put("name", name);
        XYPair point1Meters = point1.clone().add(this.fieldOffset.getPoint()).scale(1 / BasePoseSubsystem.INCHES_IN_A_METER);
        XYPair point2Meters = point2.clone().add(this.fieldOffset.getPoint()).scale(1 / BasePoseSubsystem.INCHES_IN_A_METER);
        data.put("point_1", new JSONArray(new double[] {point1Meters.x, point1Meters.y, zIndex / BasePoseSubsystem.INCHES_IN_A_METER}));
        data.put("point_2", new JSONArray(new double[] {point2Meters.x, point2Meters.y, zIndex / BasePoseSubsystem.INCHES_IN_A_METER}));
        data.put("color", new JSONArray(new double[] {color.red, color.green, color.blue}));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + robotPort + "/overlay/line"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // ok
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void drawArrow(String name, XYPair point1, XYPair point2, Color color, float zIndex) {
        JSONObject data = new JSONObject();
        data.put("name", name);
        XYPair point1Meters = point1.clone().add(this.fieldOffset.getPoint()).scale(1 / BasePoseSubsystem.INCHES_IN_A_METER);
        XYPair point2Meters = point2.clone().add(this.fieldOffset.getPoint()).scale(1 / BasePoseSubsystem.INCHES_IN_A_METER);
        data.put("point_1", new JSONArray(new double[] {point1Meters.x, point1Meters.y, zIndex / BasePoseSubsystem.INCHES_IN_A_METER}));
        data.put("point_2", new JSONArray(new double[] {point2Meters.x, point2Meters.y, zIndex / BasePoseSubsystem.INCHES_IN_A_METER}));
        data.put("color", new JSONArray(new double[] {color.red, color.green, color.blue}));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + robotPort + "/overlay/arrow"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // ok
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void drawCircle(String name, XYPair center, float radius, Color color, float zIndex) {
        JSONObject data = new JSONObject();
        data.put("name", name);
        XYPair centerMeters = center.clone().add(this.fieldOffset.getPoint()).scale(1 / BasePoseSubsystem.INCHES_IN_A_METER);
        data.put("center", new JSONArray(new double[] {centerMeters.x, centerMeters.y, zIndex / BasePoseSubsystem.INCHES_IN_A_METER}));
        data.put("color", new JSONArray(new double[] {color.red, color.green, color.blue}));
        data.put("radius", radius);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + robotPort + "/overlay/circle"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // ok
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static HttpClient buildHttpClient(boolean enableProxy, int proxyPort) {
        Builder clientBuilder = HttpClient.newBuilder().version(Version.HTTP_1_1);

        if (enableProxy) {
            clientBuilder.proxy(ProxySelector.of(InetSocketAddress.createUnresolved("localhost", proxyPort)));
        }

        return clientBuilder.build();
    }

}
