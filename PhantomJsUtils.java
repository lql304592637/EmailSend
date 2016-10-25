/**
 * Reconfigurable Manufacturing Execution Systems (RMES)
 * 
 * Copyright (c) 2016 Lean Intelligence Inc., All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL or COMMERCIAL purposes and without fee is
 * hereby granted.
 * 
 * YOUR COMPANY MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. YOUR COMPANY SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE PERFORMANCE,
 * SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT NAVIGATION OR
 * COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE SUPPORT MACHINES, OR
 * WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE SOFTWARE COULD LEAD DIRECTLY TO
 * DEATH, PERSONAL INJURY, OR SEVERE PHYSICAL OR ENVIRONMENTAL DAMAGE
 * ("HIGH RISK ACTIVITIES"). YOUR COMPANY SPECIFICALLY DISCLAIMS ANY EXPRESS OR
 * IMPLIED WARRANTY OF FITNESS FOR HIGH RISK ACTIVITIES.
 */

package com.sophlean.core.util;

import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Named("phantomJsUtils")
@Scope("session")
public class PhantomJsUtils {

	public String generate(String jsFilePath, String url, String rootPath,String targetFilePath, int width, int height) {
		String command = StringUtils.join("C:/Users/lql/Desktop/phantomjs-2.1.1-windows/bin/phantomjs ", jsFilePath, " ", url, " ", rootPath + targetFilePath, " ", width, " ", height);

		StringBuilder output = new StringBuilder();
		try {
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			while ((line = bri.readLine()) != null) {
				output.append(line).append("\r\n");
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				output.append(line).append("\r\n");
			}
			bre.close();
			p.waitFor();
		} catch (Exception ex) {
			output.append(ex);
		}
		return targetFilePath;
	}
}
