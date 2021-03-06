/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Deploy
* (c) Copyright IBM Corporation 2011, 2015. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

import com.ibm.rational.air.plugin.ios.Util;

Util.assertMacOS();
if(!Util.isSimulatorRunning()) {
    println "No simulator was running.";
    System.exit(-1);
}

Util.stopSimulator();

if(Util.isSimulatorRunning()) {
    println "The simulator failed to stop.";
    System.exit(-1);
}

println "The simulator stopped.";
System.exit(0);