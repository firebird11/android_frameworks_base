/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.server.location.timezone;

import static com.android.server.location.timezone.LocationTimeZoneManagerService.PRIMARY_PROVIDER_NAME;
import static com.android.server.location.timezone.LocationTimeZoneManagerService.SECONDARY_PROVIDER_NAME;

import android.annotation.NonNull;
import android.os.Bundle;
import android.os.ShellCommand;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/** Implements the shell command interface for {@link LocationTimeZoneManagerService}. */
class LocationTimeZoneManagerShellCommand extends ShellCommand {

    private static final List<String> VALID_PROVIDER_NAMES =
            Arrays.asList(PRIMARY_PROVIDER_NAME, SECONDARY_PROVIDER_NAME);

    private static final String CMD_START = "start";
    private static final String CMD_STOP = "stop";
    private static final String CMD_SEND_PROVIDER_TEST_COMMAND = "send_provider_test_command";

    private final LocationTimeZoneManagerService mService;

    LocationTimeZoneManagerShellCommand(LocationTimeZoneManagerService service) {
        mService = service;
    }

    @Override
    public int onCommand(String cmd) {
        if (cmd == null) {
            return handleDefaultCommands(cmd);
        }

        switch (cmd) {
            case CMD_START: {
                return runStart();
            }
            case CMD_STOP: {
                return runStop();
            }
            case CMD_SEND_PROVIDER_TEST_COMMAND: {
                return runSendProviderTestCommand();
            }
            default: {
                return handleDefaultCommands(cmd);
            }
        }
    }

    @Override
    public void onHelp() {
        final PrintWriter pw = getOutPrintWriter();
        pw.println("Location Time Zone Manager (location_time_zone_manager) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.printf("  %s\n", CMD_START);
        pw.println("    Starts the location_time_zone_manager, creating time zone providers.");
        pw.printf("  %s\n", CMD_STOP);
        pw.println("    Stops the location_time_zone_manager, destroying time zone providers.");
        pw.printf("  %s <provider name> <test command>\n",
                CMD_SEND_PROVIDER_TEST_COMMAND);
        pw.println("    Passes a test command to the named provider.");
        pw.println();
        pw.printf("%s details:\n", CMD_SEND_PROVIDER_TEST_COMMAND);
        pw.println();
        pw.printf("<provider name> = One of %s\n", VALID_PROVIDER_NAMES);
        pw.println();
        pw.println("<test command> encoding:");
        pw.println();
        TestCommand.printShellCommandEncodingHelp(pw);
        pw.println();
        pw.printf("Provider modes can be modified by setting the \"%s<provider name>\" system"
                        + " property and restarting the service or rebooting the device.\n",
                LocationTimeZoneManagerService.PROVIDER_MODE_OVERRIDE_SYSTEM_PROPERTY_PREFIX);
        pw.println("Values are:");
        pw.printf("  %s - simulation mode (see below for commands)\n",
                LocationTimeZoneManagerService.PROVIDER_MODE_SIMULATED);
        pw.printf("  %s - disabled mode\n", LocationTimeZoneManagerService.PROVIDER_MODE_DISABLED);
        pw.println();
        pw.println("Simulated providers can be used to test the system server behavior or to"
                + " reproduce bugs without the complexity of using real providers.");
        pw.println();
        pw.println("The test commands for simulated providers are:");
        SimulatedLocationTimeZoneProviderProxy.printTestCommandShellHelp(pw);
        pw.println();
        pw.println("Test commands cannot currently be passed to real provider implementations.");
        pw.println();
    }

    private int runStart() {
        try {
            mService.start();
        } catch (RuntimeException e) {
            reportError(e);
            return 1;
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Service started");
        return 0;
    }

    private int runStop() {
        try {
            mService.stop();
        } catch (RuntimeException e) {
            reportError(e);
            return 1;
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Service stopped");
        return 0;
    }

    private int runSendProviderTestCommand() {
        PrintWriter outPrintWriter = getOutPrintWriter();

        String providerName;
        TestCommand testCommand;
        try {
            providerName = validateProviderName(getNextArgRequired());
            testCommand = createTestCommandFromNextShellArg();
        } catch (RuntimeException e) {
            reportError(e);
            return 1;
        }

        outPrintWriter.println("Injecting testCommand=" + testCommand
                + " to providerName=" + providerName);
        try {
            Bundle result = mService.handleProviderTestCommand(providerName, testCommand);
            outPrintWriter.println(result);
        } catch (RuntimeException e) {
            reportError(e);
            return 2;
        }
        return 0;
    }

    @NonNull
    private TestCommand createTestCommandFromNextShellArg() {
        return TestCommand.createFromShellCommandArgs(this);
    }

    private void reportError(Throwable e) {
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Error: ");
        e.printStackTrace(errPrintWriter);
    }

    @NonNull
    static String validateProviderName(@NonNull String value) {
        if (!VALID_PROVIDER_NAMES.contains(value)) {
            throw new IllegalArgumentException("Unknown provider name=" + value);
        }
        return value;
    }
}
