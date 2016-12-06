package org.aksw.sdw.meta_rdf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

import org.aksw.sdw.meta_rdf.file.metafile.MetaStatementsUnit;
import org.aksw.sdw.meta_rdf.file.representations.AbstractRepresentationFormat;
import org.aksw.sdw.meta_rdf.file.representations.CompanionPropertyRepresentation;
import org.aksw.sdw.meta_rdf.file.representations.GraphRepresentation;
import org.aksw.sdw.meta_rdf.file.representations.NaryRelationRepresentation;
import org.aksw.sdw.meta_rdf.file.representations.RawDataRepresentation;
import org.aksw.sdw.meta_rdf.file.representations.RdrRepresentation;
import org.aksw.sdw.meta_rdf.file.representations.SingletonPropertyRepresentation;
import org.aksw.sdw.meta_rdf.file.representations.StandardReificationRepresentation;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import one.util.streamex.StreamEx;

	

public class Main 
{

    static String inputFilePath ;
    static String outputFilePattern ;
    static int numThreads = 0;
    static Map<AbstractRepresentationFormat,PrintStream> representations = new HashMap<>();	
    static AtomicInteger linesCount = new AtomicInteger(0);

	
	public static void main(String[] args) 
	{

		try
		{
			parseArguments(args);
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		InputStream is = null;
		InputStream in = null;//ArchiveInputStream in =null;
		try
		{
			is = new BufferedInputStream(new FileInputStream(inputFilePath));
			//is = new FileInputStream(inputFilePath); System.out.println(is.markSupported());
			in = new CompressorStreamFactory().createCompressorInputStream(is);
			//in = new ArchiveStreamFactory().createArchiveInputStream(is);
			 //ZipArchiveEntry entry = (ZipArchiveEntry)in.getNextEntry();
		} catch (FileNotFoundException /*| ArchiveException*/ | CompressorException e1)
		{
			// TODO Auto-generated catch block
			System.out.println("no compressed Format detected assuming plain text file");
			in = is;
		}  
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) { //Files.newBufferedReader(Paths.get(inputFilePath))) {
			try
			{
				if (numThreads>0)
					System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", ""+numThreads);
				//ps = new PrintStream(Paths.get(outputFilePattern).toFile());
				//br.lines().unordered().parallel().forEach(Main::processLine);
				StreamEx.ofLines(br).unordered().parallel().forEach(Main::processLine);
			} 
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			finally {
				for (AbstractRepresentationFormat r : representations.keySet())
				{
					
				    PrintStream ps = representations.get(r);
				    r.writeQuads(r.getDeduplicated(), ps);
					ps.close();
				}
				
			}		 

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	static void parseArguments(String[] args) throws IOException
	{
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file path pattern (file name pattern without file suffix)");
		//output.setRequired(true);
		options.addOption(output);
		
		Option threads = new Option("t", "threads", true, "the number of threads which should be used for parallel reading");
		//output.setRequired(true);
		options.addOption(threads);
		
		Option format = new Option("f", "formats", false, "list of representation formats which should be used or all (default)");
		//format.setRequired(true); 
		format.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(format);
		
		Option properties  = OptionBuilder.withArgName( "property=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "use value for given property" )
                .create( "D" );
		options.addOption(properties);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("meta-rdf", options);

			System.exit(1);
			return;
		}

        inputFilePath = cmd.getOptionValue("input");
        outputFilePattern = cmd.getOptionValue("output");
        Meta.options =new org.aksw.sdw.meta_rdf.Options(cmd.getOptionProperties("D"));
        numThreads = cmd.hasOption("threads") ? Integer.parseInt(cmd.getOptionValue("threads")) : 0;
        
        String all[] = {"all"};  
        String formats[] = (cmd.getOptionValues("formats")!=null) ? cmd.getOptionValues("formats") : all;
        
        for (String f :  formats)
		{
			AbstractRepresentationFormat g;
			if (f.equalsIgnoreCase("ngraphs") || f.equalsIgnoreCase("all"))
				representations.put(	g = new GraphRepresentation(),   					createPS(outputFilePattern+	"-ngraphs."	+g.getFileExtension() )   );
			if (f.equalsIgnoreCase("sgprop")  || f.equalsIgnoreCase("all"))
				representations.put(	g = new SingletonPropertyRepresentation(), 			createPS(outputFilePattern+	"-sgprop."	+g.getFileExtension() )   );		
			if (f.equalsIgnoreCase("stdreif") || f.equalsIgnoreCase("all"))
				representations.put(	g = new StandardReificationRepresentation(), 		createPS(outputFilePattern+	"-stdreif."	+g.getFileExtension() )   );		
			if (f.equalsIgnoreCase("rdr")     || f.equalsIgnoreCase("all"))
				representations.put(	g = new RdrRepresentation(), 						createPS(outputFilePattern+	"-rdr."		+g.getFileExtension() )   );		
			if (f.equalsIgnoreCase("cpprop")  || f.equalsIgnoreCase("all"))
				representations.put(	g = new CompanionPropertyRepresentation(), 			createPS(outputFilePattern+	"-cpprop."	+g.getFileExtension() )   );
			if (f.equalsIgnoreCase("nary")    || f.equalsIgnoreCase("all"))
				representations.put(	g = new NaryRelationRepresentation(), 			    createPS(outputFilePattern+	"-nary."	+g.getFileExtension() )   );
			if (f.equalsIgnoreCase("data")    || f.equalsIgnoreCase("all"))
				representations.put(	g = new RawDataRepresentation(), 			    	createPS(outputFilePattern+	"-data."	+g.getFileExtension() )   );
		}

    }
	
	static PrintStream createPS(String filename) throws IOException
	{
		if (Boolean.parseBoolean(Meta.getOptions().getProperties().getProperty("gzOutput","false")))
			return new PrintStream(new GZIPOutputStream(new FileOutputStream(Paths.get(filename+".gz").toFile())));
		else
			return new PrintStream(Paths.get(filename).toFile());
	}
	
	static void processLine(String line)
	{
		int thisLineNr = linesCount.getAndIncrement();
		if (thisLineNr % 1000 == 0)
			System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date())+" processed units: "+linesCount);
		MetaStatementsUnit msu;
		try
		{
			msu = Meta.readMetaStatementsUnit(line);
			for (AbstractRepresentationFormat r : representations.keySet())
			{
			    PrintStream ps = representations.get(r);
			    Collection<RdfQuad> l = r.getRepresenationForUnit(msu);
				r.writeQuads(l, ps);
			}
		} catch (Exception e)
		{
			System.err.println("An Error occured during processing the metastatementsUnit #"+thisLineNr);
			e.printStackTrace();
			System.err.println(line);
		}
		
//		AbstractRepresentationFormat f = new GraphRepresentation();
//		Collection<RdfQuad> l = f.getRepresenationForUnit(msu);
//		Meta.writeQuads(l, ps);
//		try(PrintStream ps = new PrintStream(Paths.get(outputFilePath).toFile(),))
//		{
//			Meta.writeQuads(l, ps);//FileOutputStream(Paths.get(outputFilePath).toFile()));
//		} catch (FileNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
	}

}
