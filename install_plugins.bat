REM Run this script inside your eclipse install folder
eclipsec.exe -application org.eclipse.equinox.p2.director -repository http://download.eclipse.org/releases/mars -installIU org.eclipse.jdt.feature.group
eclipsec.exe -application org.eclipse.equinox.p2.director -repository http://first.wpi.edu/FRC/roborio/release/eclipse/ -installIU edu.wpi.first.wpilib.plugins.java.feature.feature.group
eclipsec.exe -application org.eclipse.equinox.p2.director -repository http://infinitest.github.io -installIU org.infinitest.eclipse.feature.feature.group
eclipsec.exe -application org.eclipse.equinox.p2.director -repository http://eclipse-cs.sf.net/update -installIU net.sf.eclipsecs.feature.group
