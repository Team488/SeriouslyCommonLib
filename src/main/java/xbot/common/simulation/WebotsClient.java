package xbot.common.simulation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.json.JSONObject;
import org.json.JSONArray;

import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

@Singleton
public class WebotsClient {
    final DoubleProperty simulatorPoseX;
    final DoubleProperty simulatorPoseY;
    final DoubleProperty simulatorPoseYaw;
    final String hostname = "127.0.0.1";
    final int supervisorPort = 10001;
    int robotPort = -1;

    HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build();

    @Inject
    public WebotsClient(PropertyFactory propertyFactory) {
        propertyFactory.setPrefix("Webots");
        simulatorPoseX = propertyFactory.createEphemeralProperty("Simulator Pose X", 0);
        simulatorPoseY = propertyFactory.createEphemeralProperty("Simulator Pose Y", 0);
        simulatorPoseYaw = propertyFactory.createEphemeralProperty("Simulator Pose Yaw", 0);
    }

    public void initialize() {
        // Spawn a robot in the sim
        JSONObject data = new JSONObject();
        data.put("template", "HttpRobotTemplate");

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

    public JSONObject buildMotorObject(String motor, float value) {
        JSONObject result = new JSONObject();
        result.put("id", motor);
        result.put("val", value);
        return result;
    }

    public JSONObject sendMotors(List<MockCANTalon> motors) {
        JSONObject data = new JSONObject();
        List<JSONObject> motorValues = new ArrayList<JSONObject>();

        for (MockCANTalon motor : motors) {
            motorValues.add(buildMotorObject("Motor" + motor.deviceId, (float) motor.getThrottlePercent()));
        }

        data.put("motors", motorValues);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + hostname + ":" + robotPort + "/motors"))
                .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(data.toString())).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // parse response for sensor values
                JSONObject responseData = new JSONObject(response.body());
                JSONArray sensors = (JSONArray) responseData.get("Sensors");
                handleSimulatorPose((JSONObject)responseData.get("WorldPose"));
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
        simulatorPoseX.set(positionArray.getDouble(0));
        simulatorPoseY.set(positionArray.getDouble(1));
        simulatorPoseYaw.set(worldPose.getDouble("Yaw"));
    }

    public void resetPosition() {
        resetPosition(0, 0, 0);
    }

    public void resetPosition(double x, double y, double rotationInDegrees) {
        
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
                JSONObject responseData = new JSONObject(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
