/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Deploy
* (c) Copyright IBM Corporation 2011, 2014. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

import com.urbancode.air.AirPluginTool;
import com.ibm.rational.air.plugin.ios.Util;

def apTool = new AirPluginTool(this.args[0], this.args[1]);
final def props = apTool.getStepProperties();

def udid = props['udid']
def simName = props['simName']
def simDeviceType = props['simDeviceType']?: ""
def targetOS = props['targetOS']
def xcrunPath = props['xcrunPath']

Util.assertMacOS();

if (udid) {
    Util.isUDIDValid(xcrunPath, udid);
    if(!Util.isSimUDID(udid)){
        println "Error: The simulator unique device identifier " + 
            "(UDID) " + udid + " to delete was not found."
        System.exit(-1);
    }
} else {
    udid = Util.findSimulatorUDID(simName, simDeviceType.trim(), targetOS.trim(), xcrunPath);
}

println "The simulator " + udid + " will be deleted.";
Util.deleteSimulator(udid, xcrunPath);

println "The simulator was deleted.";
System.exit(0);