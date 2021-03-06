/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Deploy
* (c) Copyright IBM Corporation 2011, 2015. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

import com.urbancode.air.AirPluginTool;
import com.urbancode.air.CommandHelper;
import com.ibm.rational.air.plugin.ios.Util;

def apTool = new AirPluginTool(this.args[0], this.args[1]);
final def props = apTool.getStepProperties();

def app = props['app']
def script = props['script']
def outputDir = props['outputDir']?: "."
def udid = props['udid']
def simName = props['simName']
def simDeviceType = props['simDeviceType']?: ""
def targetOS = props['targetOS']
def xcode = props['xcode']?: "/Applications/Xcode.app"
def traceTemplate = props['traceTemplate']
def xcrunPath = props['xcrunPath']
def timeout = props['timeout']

Util.assertMacOS();

def args = ["instruments"];
def appFile = Util.handleApplication(app);
// Target the physical device, if one is specified.
if(udid) {
    Util.isUDIDValid(xcrunPath, udid);
    if (Util.isSimUDID(udid)){
        Util.isAppValidForSimArch(udid, appFile);
    } else {
        Util.isAppValidForDeviceArch(appFile);
    }
} else {
    // Check if only one of the simulator target properties are set.
    if(!(simName && targetOS)) {
        println "Error: Both the Simulator Name and Target OS must be specified when " +
            "the simulator target is specified for the UI test.";
        println "Explanation: This error can occur if neither Simulator Name nor Target OS are defined.";
        println "User response: Verify that the Simulator Name and Target OS, or " +
            "Device Identifier are defined for the UI Test step.";
        System.exit(-1);
    }
    udid = Util.findSimulatorUDID(simName, simDeviceType.trim(), targetOS.trim(), xcrunPath);
    Util.isAppValidForSimArch(udid, appFile);
}

// Build up the String for the target using the UDID of the device or simulator,
// if none is specified, the default Simulator is used.
if(udid) {
    args << "-w";
    args << udid.trim();
}

def xcodeApp = Util.verifyXcodePath(xcode);

if(!traceTemplate) {
    traceTemplate = new String(File.separator + "Contents" + File.separator + "Applications" +
        File.separator + "Instruments.app" + File.separator + "Contents" + File.separator +
        "PlugIns" + File.separator + "AutomationInstrument.xrplugin" + File.separator +
        "Contents" + File.separator + "Resources" + File.separator +
        "Automation.tracetemplate");
}

def traceTemplatePath;
try {
    traceTemplatePath = new File(xcodeApp, traceTemplate);
} catch (Exception e) {
    println "An error occurred during an attempt to access the trace " +
        "template: " + e.getMessage();
    System.exit(-1);
}
if(!traceTemplatePath.file) {
    println "Error: The path to the trace template is incorrect: " +
        traceTemplatePath.canonicalPath;
    System.exit(-1);
}

args << "-t";
args << traceTemplatePath.canonicalPath;

args << appFile;

def scriptFile;
try {
    scriptFile = new File(script);
} catch (Exception e) {
    println "An error occurred during an attempt to access the script " +
        "file " + script + ": " + e.getMessage();
    System.exit(-1);
}
if(!scriptFile.file) {
    println "Error: The path to the script file is incorrect: " +
        scriptFile.canonicalPath;
    System.exit(-1);
}

args << "-e" << "UIASCRIPT" << script << "-v";

System.exit(Util.uiTest(xcrunPath, outputDir, "Running UI tests.", args, timeout));