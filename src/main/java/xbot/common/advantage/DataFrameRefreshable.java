package xbot.common.advantage;

/**
 * Interface for objects that need to have state refreshed before the subsystem periodic loop.
 * <pre class="mermaid">
 * graph TD
 *     A[Start scheduler loop] --> B[refreshDataFrame]
 *     B --> C[periodic]
 *     C -->D[End scheduler loop]
 *     D --> A
 * </pre>
 */
public interface DataFrameRefreshable {

    /**
     * Consumes and processes inputs from the device or subsystem.
     * @apiNote This method is called before {@link xbot.common.command.BaseSubsystem#periodic}
     * to update the object state before the scheduler runs periodic() on all the subsystems.
     * You can call {@link xbot.common.command.BaseSubsystem#registerDataFrameRefreshable(DataFrameRefreshable)}
     * to have this method called automatically by the subsystem.
     */
    void refreshDataFrame();

}
