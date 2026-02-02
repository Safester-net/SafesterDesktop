/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package com.safelogic.pgp.api.toolkit.xmlrpc;

//ServerParmsManager
//Copyright (c) SafeLogic, 2000 - 2005
//
//Last Updates: 
//19/11/09 14:35 NDP : Comments & modify USAGE


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.safelogic.pgp.api.toolkit.CgeepCmdLineOption;

public class ServerParmsManager {
	
	//Avalaible options
	private static String[] HELP		= {"h", "help"};
	private static String[] PORT		= {"port"};
	private static String[] PARANOID	= {"paranoid"};
	
	
	private static final String CR_LF = System.getProperty("line.separator");
	
	//Help message
	private static String USAGE = "Usage: com.safelogic.pgp.api.toolkit.xmlrpc.Server " + CR_LF +
			"-h                       Print this message" + CR_LF +
			"--port <port>            Specify server port (default value is 8081)" + CR_LF +
			"--paranoid <on|off>      Set paranoid mode on or off (default is on)" + CR_LF;
	
	
	/** The default port in use */
	private int port = 8081;
	
	/** The paranoid mode */
	private String paranoid = "on";
	
	public ServerParmsManager()
	{
		
	}
	
	/**
	 * Create all options
	 * @return
	 */
	private Options buildOptions()
	{
		Options options = new Options();
		List<CgeepCmdLineOption> optionsList = buildOptionsList();

		for(CgeepCmdLineOption cmdLineOption : optionsList)
		{
			Option option = getOption(cmdLineOption);
			options.addOption(option);		
		}

		return options;
	}
	
	/**
	 * Create the list of available options
	 * @return
	 */
	private List<CgeepCmdLineOption> buildOptionsList()
	{
		List<CgeepCmdLineOption> optionsList = new Vector<CgeepCmdLineOption>();

		CgeepCmdLineOption optionHelp = new CgeepCmdLineOption(HELP[0], HELP[1], "Print help message.");
		optionsList.add(optionHelp);
		
		CgeepCmdLineOption optionPort = new CgeepCmdLineOption(null, PORT[0], "Server port");
		optionPort.addArg("port", "port (default 8080)");
		optionPort.setOptional(false);
		
		optionsList.add(optionPort);
		
		
		CgeepCmdLineOption optionParanoid = new CgeepCmdLineOption(null, PARANOID[0], "Paranoid mode");
		optionParanoid.addArg("value", "on / off (default on)");
		optionParanoid.setOptional(false);
		
		optionsList.add(optionParanoid);
		
		return optionsList;
	}

	/**
	 * Creates Option object from CgeepCmdLineOption
	 * @param cmdLineOption  	the CgeepCmdLineOption
	 * @return					the Option object
	 */
	private Option getOption(CgeepCmdLineOption cmdLineOption) {

		Map<String, String> args = cmdLineOption.getArguments();

		OptionBuilder.withValueSeparator();
		OptionBuilder.withDescription(cmdLineOption.getDesc());
		OptionBuilder.withLongOpt(cmdLineOption.getLongName());

		if(args != null)
		{
			int nbArgs = cmdLineOption.getArguments().size();
			OptionBuilder.hasArgs(nbArgs);
			Set<String > argsName = args.keySet();
			int i = 0;
			for(String key : argsName)
			{
				i++;
				String argumentDef = key + "=" + args.get(key);
				OptionBuilder.withArgName(argumentDef);
			}	

			if(cmdLineOption.isOptional())
			{
				OptionBuilder.hasOptionalArgs();
			}
		}

		if(cmdLineOption.getShortName() != null)
		{
			return OptionBuilder.create(cmdLineOption.getShortName());
		}

		return OptionBuilder.create();
	}

    /**
     * Execute the command line
     * @param args      the  args 
     */
    public boolean executeCommand(String[] args) 
        // Full qualification because ParseException is a common name!!! So silly!!!
    {        

        try
        {
            Options options = buildOptions();

            CommandLineParser parser = new PosixParser();
            CommandLine cmd = null;
                                    
            cmd = parser.parse( options, args);
            
            if (args.length != 0)
            {
                if (! cmd.hasOption(HELP[0]) 
                        && ! cmd.hasOption(PORT[0])
                        && ! cmd.hasOption(PARANOID[0]))
                {
                    printUsage(false);
                    return false;
                }
            }
            
            if(cmd.hasOption(HELP[0]))
            {
            	printUsage(false);
            	return false;
            }
                        
            if(cmd.hasOption(PORT[0]))
            {                
                String sPort  = cmd.getOptionValue(PORT[0]);
            	if(sPort == null)
            	{
            		String []values = cmd.getArgs();
            	
            		if(values == null)
            		{
            			printUsage(true);
            			return false;

            		}
            		sPort = values[0];
            	}
            	
            	try
            	{
            		this.port = Integer.parseInt(sPort);
            	}
            	catch (NumberFormatException e) {
            		System.out.println("Bad argument!");
            		printUsage(false);
            		return false;
            	}
            }
            
            if(cmd.hasOption(PARANOID[0]))
            {
            	String  mode = cmd.getOptionValue(PARANOID[0]);
            	if(mode == null)
            	{
            		String []values = cmd.getArgs();
            	
            		if(values == null)
            		{
            			printUsage(true);
            			return false;
            		}
            		mode = values[0];
            	}
            	if(mode.equalsIgnoreCase("on") || mode.equalsIgnoreCase("off"))
            	{
            		this.paranoid = mode;
            	}
            	else
            	{
            		System.out.println("Bad argument!");
            		printUsage(false);
                    return false;
            	}
            }
            
            // Ok
            return true;
        }
        catch (org.apache.commons.cli.ParseException e)
        {
            System.out.println("Bad argument!");
            printUsage(false);
            return false;
        }
        
    }
        
    /**
     * Get port number
     * @return
     */
	public int getPort() {
		return port;
	}

	/**
	 * Set port number
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get paranoid mode 
	 * @return on|off
	 */
	public String getParanoid() {
		return paranoid;
	}

	/**
	 * Set paranoid mode
	 * @param paranoid
	 */
	public void setParanoid(String paranoid) {
		this.paranoid = paranoid;
	}

	
	/**
	 * Print help message
	 * @param missingArgs
	 */
	private static void printUsage(boolean missingArgs)
	{
		if(missingArgs)
		{
			System.out.println("Missing arguments!");
		}
		System.out.println(USAGE);
		
	}
	
	public static void main(String args[])
	{

	}
}
