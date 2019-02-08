package xbot.common.math;

/*
 *******************************************************************************************
 * Copyright (C) 2017 FRC Team 1736 Robot Casserole - www.robotcasserole.org
 *******************************************************************************************
*/

import java.util.Arrays;

import xbot.common.controls.sensors.XTimer;

public class InterpolatingHistoryBuffer {

    // I manually implemented a circular buffer cuz I enjoy difficult things like
    // that.
    private int bufferSize; // size of buffers
    private double[] value_buffer; // circular buffer to hold all values
    private double[] time_buffer; // circular buffer to hold all values
    private int index; // "pointer" to the starting index in the buffers

    /**
     * This class implements a set of circular buffers that can be used to store
     * values of signals in the past, and retrieve values at arbitrary previous time
     * indexes.
     * 
     * The value is assumed to saturate at the end of the maintained history.
     * 
     * @param length   Number of samples to keep
     * @param init_val Value to fill the inital samples with (probably zero is fine)
     */
    public InterpolatingHistoryBuffer(int length, double init_val) {
        double init_time = XTimer.getFPGATimestamp();
        value_buffer = new double[length];
        time_buffer = new double[length];
        Arrays.fill(value_buffer, init_val);
        Arrays.fill(time_buffer, init_time);
        index = 0;
        bufferSize = length;
    }

    /**
     * Insert a new value into the buffer. Discards the oldest value.
     * 
     * @param time  Time at which the value was sampled. elements must be inserted
     *              in a monotomically increasing fashion
     * @param value Value of the signal right now
     * @return true if the value was inserted, false otherwise
     */
    public boolean insert(double time, double value) {

        // Sanity check the inserted value
        if (time_buffer[index] > time) {
            System.out.println("ERROR: InterpValueHistoryBuffer got non-increasing time vector. Tell software team!");
            return false;
        }

        // Update index
        index = getNextIdx(index);

        // Insert values
        value_buffer[index] = value;
        time_buffer[index] = time;

        return true;
    }

    /**
     * Returns the value at a given time, linearlly interpolated.
     * 
     * @param time time at which to retrieve value
     * @return value at inputted time.
     */
    public double getValAtTime(double time) {
        int lower_idx = index;
        int upper_idx = getNextIdx(index);
        boolean firstPass = true;

        while (true) {
            if ((time_buffer[lower_idx] <= time) & (time_buffer[upper_idx] >= time)) {
                // Case, we've found the desired block. Calculate the value at the given time
                // with linear interpolation
                double segment_delta_t = time_buffer[upper_idx] - time_buffer[lower_idx];
                double segment_delta_v = value_buffer[upper_idx] - value_buffer[lower_idx];
                double time_ratio = (time - time_buffer[lower_idx]) / segment_delta_t;
                return value_buffer[lower_idx] + (segment_delta_v * time_ratio);
            }

            // Update iteration indicies for next loop
            lower_idx = getNextIdx(lower_idx);
            upper_idx = getNextIdx(upper_idx);

            if (lower_idx == index && !firstPass) {
                // Terminal case, we're off the end. Return the oldest value.
                return value_buffer[lower_idx];
            } else {
                firstPass = false;
            }
        }
    }

    private int getNextIdx(int idx) {
        if (idx + 1 >= bufferSize) {
            return 0;
        } else {
            return idx + 1;
        }
    }

}