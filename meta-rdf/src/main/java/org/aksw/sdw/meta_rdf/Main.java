package org.aksw.sdw.meta_rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main 
{

	public static void main(String[] args) 
	{
		String fileName = "c://lines.txt";

		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

			 br.lines().parallel().forEach(Main::processLine);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	static void processLine(String line)
	{
		line.substring(0,line.indexOf('\t', 0)-1);
	}

}
