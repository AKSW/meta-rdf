package org.aksw.sdw.meta_rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
	

public class Main 
{

    static String inputFilePath ;
    static String outputFilePath ;
	
	public static void main(String[] args) 
	{

		parseArguments(args);
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(inputFilePath))) {

			 br.lines().parallel().forEach(Main::processLine);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	static void parseArguments(String[] args)
	{
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file");
		//output.setRequired(true);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
			return;
		}

        inputFilePath = cmd.getOptionValue("input");
        outputFilePath = cmd.getOptionValue("output");

    }
	
	static void processLine(String line)
	{
		Meta.readMetaStatementsUnit(line);
	}

}
