package xbot.common.injection.electrical_contract;

/**
 * The per-module wiring of one swerve module: the only swerve values that differ between modules. Everything shared
 * (controller type, output config, CAN bus, naming) comes from the contract's other swerve hooks; see
 * {@link XSwerveDriveElectricalContract#getSwerveModuleWiring}.
 *
 * <p>
 * Build with named steps so each ID is labeled where it's written:
 *
 * <pre>
 * SwerveModuleWiring.builder().withDrive(30, PDHPort.PDH10).withSteering(31, PDHPort.PDH11).withEncoder(53).build();
 * SwerveModuleWiring.builder().withDrive(31).withSteering(30).withEncoder(51).build(); // PDH ports not tracked
 * </pre>
 *
 * @param driveCanId
 *            CAN ID of the drive motor controller.
 * @param drivePdh
 *            PDH port powering the drive motor, or null if not tracked.
 * @param steeringCanId
 *            CAN ID of the steering motor controller.
 * @param steeringPdh
 *            PDH port powering the steering motor, or null if not tracked.
 * @param encoderCanId
 *            CAN ID of the absolute steering encoder.
 */
public record SwerveModuleWiring(int driveCanId, PDHPort drivePdh, int steeringCanId, PDHPort steeringPdh, int encoderCanId) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final int UNSET = -1;

        private int driveCanId = UNSET;
        private PDHPort drivePdh;
        private int steeringCanId = UNSET;
        private PDHPort steeringPdh;
        private int encoderCanId = UNSET;

        private Builder() {
        }

        /** The drive motor's CAN ID, with no PDH port recorded. */
        public Builder withDrive(int canId) {
            return withDrive(canId, null);
        }

        /** The drive motor's CAN ID and the PDH port powering it. */
        public Builder withDrive(int canId, PDHPort pdhPort) {
            this.driveCanId = canId;
            this.drivePdh = pdhPort;
            return this;
        }

        /** The steering motor's CAN ID, with no PDH port recorded. */
        public Builder withSteering(int canId) {
            return withSteering(canId, null);
        }

        /** The steering motor's CAN ID and the PDH port powering it. */
        public Builder withSteering(int canId, PDHPort pdhPort) {
            this.steeringCanId = canId;
            this.steeringPdh = pdhPort;
            return this;
        }

        /** The absolute steering encoder's CAN ID. */
        public Builder withEncoder(int canId) {
            this.encoderCanId = canId;
            return this;
        }

        public SwerveModuleWiring build() {
            if (driveCanId == UNSET || steeringCanId == UNSET || encoderCanId == UNSET) {
                throw new IllegalStateException("SwerveModuleWiring needs drive, steering and encoder CAN IDs");
            }
            return new SwerveModuleWiring(driveCanId, drivePdh, steeringCanId, steeringPdh, encoderCanId);
        }
    }
}
