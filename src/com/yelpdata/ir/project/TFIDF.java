package com.yelpdata.ir.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class TFIDF {

	public static String removeStopWords(String textFile) throws Exception {
	    //CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		
		StandardAnalyzer analyser = new StandardAnalyzer();
	    TokenStream tokenStream = new StandardTokenizer(new StringReader(textFile.trim()));
	    tokenStream = new StopFilter(tokenStream, analyser.STOP_WORDS_SET);
	    
	    BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"));
	    String line = null;
	    List<String> stopw = new ArrayList<String>();
	    int i =0;
	    line = br.readLine();
	   while(line != null){
		   stopw.add(line);
		   line = br.readLine();
		}
	    tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(stopw));
	    
	    StringBuilder sb = new StringBuilder();
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	    tokenStream.reset();
	    while (tokenStream.incrementToken()) {
	        String term = charTermAttribute.toString();
	        if(term.length() > 2)
	        	sb.append(term + " ");
	    }
	    return sb.toString();
	}

	public static List<String> getTop(HashMap<String,Integer> map, int limit){
		
		  ValueComparator bvc =  new ValueComparator(map);
	      TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
	      
	      sorted_map.putAll(map);
	      
		//HashMap<String, Integer> sorted = new HashMap<String,Integer>();
		 //System.out.println(sortByValue(map).toString());	
	     // System.out.println(map.toString());
		//sorted = (HashMap<String, Integer>) sortByValue(map);
			//System.out.println(sorted_map.toString());
	      Iterator it = sorted_map.entrySet().iterator();
	      List<String> topWords = new ArrayList<String>();
	      int count = 1;
		    while (it.hasNext()) {
		    	Map.Entry keyVal = (Map.Entry)it.next();
		    	if(count < limit){
		    		//System.out.println(keyVal.getKey().toString()+"   "+keyVal.getValue().toString());
		    		topWords.add(keyVal.getKey().toString());
		    	}
		    	else
		    		break;
		    	count++;
		    }
	      return topWords;
	}
	public static List<String> getTopDouble(HashMap<String,Double> map, int limit){
		
		ValueComparatorDouble bvc =  new ValueComparatorDouble(map);
	      TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
	      
	      sorted_map.putAll(map);
	      
		//HashMap<String, Integer> sorted = new HashMap<String,Integer>();
		 //System.out.println(sortByValue(map).toString());	
	     // System.out.println(map.toString());
		//sorted = (HashMap<String, Integer>) sortByValue(map);
			//System.out.println(sorted_map.toString());
	      Iterator it = sorted_map.entrySet().iterator();
	      List<String> topWords = new ArrayList<String>();
	      int count = 1;
		    while (it.hasNext()) {
		    	Map.Entry keyVal = (Map.Entry)it.next();
		    	if(count < limit){
		    		//System.out.println(keyVal.getKey().toString()+"   "+keyVal.getValue().toString());
		    		topWords.add(keyVal.getKey().toString());
		    	}
		    	else
		    		break;
		    	count++;
		    }
	      return topWords;
	}
	public static String uniqueWordExtraction(String textValue){
		Set<String> uniqueValues = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[\\s]+")));
		String finalText= "";
		for (String term : uniqueValues){
			//System.out.println("terms = "+term);
			finalText = finalText + " " +  term;
		}
		return finalText;
	}

	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	public static List<String> ReturnAdjectiveList() throws IOException
	{
			BufferedReader br = new BufferedReader(new FileReader("adjectives.txt"));
			String line = null;
			List<String> adjectives = new ArrayList<String>();
			int i =0;
			line = br.readLine();
			   
			while(line != null)
			   {
				   adjectives.add(line.toLowerCase());
				   line = br.readLine();
			   }
			
			br.close();
			return adjectives;
	}
	
	public static List<String> FindTopAdjectives(List<String> adjectives, List<String> data, int category) throws IOException
	{
		HashMap<String,Integer> wordCount = new HashMap<String,Integer>();
		
		for(int i = 0; i < data.size(); i++)
		{
			StringTokenizer defaultTokenizer = new StringTokenizer(data.get(i));
			while (defaultTokenizer.hasMoreTokens())
			{
			    String token = defaultTokenizer.nextToken();
			    if(adjectives.contains(token))
			    {
					if(wordCount.containsKey(token))
						wordCount.put(token,wordCount.get(token)+ 1 );
					else
						wordCount.put(token, 1) ;
			    }
			}
		}
		List<String> features = getTop(wordCount,20000);
		List<String> reviews = new ArrayList<String>();
		if(category == 1)
			reviews = lowReviews;
		else if(category == 2)
			reviews = medReviews;
		else
			reviews = highReviews;
		
		HashMap<String,Integer> idf = new HashMap<String,Integer>();
		
		for(int i = 0; i < features.size(); i++)
		{
			String feature = features.get(i);
			for(int j = 0; j < reviews.size(); j++)
			{
				String review = reviews.get(j);
					
				if(review.contains(feature))
				{
					if(idf.containsKey(feature))
						idf.put(feature,idf.get(feature)+ 1 );
					else
						idf.put(feature, 1) ;
				}
			}
		
		}
		return getTop(idf, 10000);
	}
	
	static List<String> globalIDF(List<String> l1,List<String>  l2, List<String> l3)
	{
		HashMap<String,Double>  globalIdf = new HashMap<String,Double>();
		for(int i = 0; i < l1.size(); i++)
		{
			int count= 1;
			
			
			String feature = l1.get(i);
			if(l2.contains(feature))
				count++;
			if(l3.contains(feature))
				count++;
			
			globalIdf.put(feature, 1.00/count);
			
		}
		/*
	    Iterator it = globalIdf.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	    }
	    */
		return getTopDouble(globalIdf, 80);
	}
	
	
	static HashMap<String,String> mapUnique = new HashMap<String,String>();
	static List<String> lowUnique = new ArrayList<String>();
	static List<String> medUnique = new ArrayList<String>();
	static List<String> highUnique = new ArrayList<String>();

	static List<String> lowCombined = new ArrayList<String>();
	static List<String> medCombined = new ArrayList<String>();
	static List<String> highCombined= new ArrayList<String>();
	
	static HashMap<String,String> mapCombined = new HashMap<String,String>();
	static List<String> lowReviews = new ArrayList<String>();
	static List<String> medReviews = new ArrayList<String>();
	static List<String> highReviews = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String path = "tp.xls";

		//File fout1 = new File("FinalBigrams.txt");
		//FileWriter fileWriter1 = new FileWriter(fout1,false);	
		
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		
		BufferedWriter bw = null;
		while(true)
		{	
			
			row = sheet.getRow(counter);
			//System.out.println(counter);
			counter++;
			//if(counter < 10000)
			//	continue;

			//if(counter == 20000)
				//break;
			if(row != null)
			{
				int stars = (int)row.getCell(0).getNumericCellValue();
				String text = row.getCell(1).getStringCellValue();
			
				String removed = removeStopWords(text.toLowerCase());
				String unique = uniqueWordExtraction(removed);

					if(stars == 1 || stars == 2)
					{
						lowReviews.add(removed);
						
						lowUnique.add(unique);
						lowCombined.add(removed);

						/*
						if(mapUnique.containsKey("low"))
							mapUnique.put("low",mapUnique.get("low")+ " "+ unique );
						else
							mapUnique.put("low", unique) ;
						
						if(mapCombined.containsKey("low"))
							mapCombined.put("low",mapCombined.get("low")+ " "+ removed );
						else
							mapCombined.put("low", removed) ;
							*/
						
					}
					else if(stars == 3)
					{
						medReviews.add(removed);
						
						medUnique.add(unique);
						medCombined.add(removed);
						/*
						if(mapUnique.containsKey("med"))
							mapUnique.put("med",mapUnique.get("med")+ " "+ unique );
						else
							mapUnique.put("med", unique) ;

						if(mapCombined.containsKey("med"))
							mapCombined.put("med",mapCombined.get("med")+ " "+ removed );
						else
							mapCombined.put("med", removed) ;
							*/			
					}
					else
					{
						
						highReviews.add(removed);
						
						highUnique.add(unique);
						highCombined.add(removed);
						/*

						if(mapUnique.containsKey("high"))
							mapUnique.put("high",mapUnique.get("high")+ " "+ unique );
						else
							mapUnique.put("high", unique) ;
						
						if(mapCombined.containsKey("high"))
							mapCombined.put("high",mapCombined.get("high")+ " "+ removed );
						else
							mapCombined.put("high", removed) ;	
							*/
					}
				counter++;
			}
			else
				break;	
		}
		
		//System.out.println("LOW****************************** :" + "\n\n\n");
		//for(int i = 0; i < lowCombined.size(); i++)
		//	System.out.println(lowCombined.get(i));
		//System.out.println("MED&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7 :" + "\n\n\n");
		//for(int i = 0; i < medCombined.size(); i++)
		//	System.out.println(medCombined.get(i));

		//System.out.println("HIGH################################ :" + "\n\n\n");
		//for(int i = 0; i < highCombined.size(); i++)
		//	System.out.println(highCombined.get(i));

		List<String> adjectives = ReturnAdjectiveList();
		List<String> lowAdjectives = null;
		List<String> medAdjectives= null;
		List<String> highAdjectives =null;
		
		if(!lowCombined.equals(" "))
		{
			lowAdjectives = FindTopAdjectives(adjectives, lowCombined, 1);
			//System.out.println("LOW :" +lowAdjectives);
		}
		if(!medCombined.equals(" "))
		{
			medAdjectives = FindTopAdjectives(adjectives, medCombined, 2);
			//System.out.println("MEDIUM: "+medAdjectives);
		}
		if(!highCombined.equals(" "))
		{
			highAdjectives = FindTopAdjectives(adjectives, highCombined, 3);
			//System.out.println("HIGH : "+highAdjectives);
		}
		
		List<String> FinalFeatures1 = globalIDF(lowAdjectives, medAdjectives, highAdjectives);
		System.out.println("LOW :" +FinalFeatures1);
		List<String> FinalFeatures2 = globalIDF(medAdjectives, lowAdjectives, highAdjectives);
		System.out.println("MED :" +FinalFeatures2);
		List<String> FinalFeatures3 = globalIDF(highAdjectives, lowAdjectives, medAdjectives);
		System.out.println("HIGH :" +FinalFeatures3);
		
		List<String> collect = new ArrayList<String>();
		
		for(int i = 0; i < FinalFeatures1.size(); i++)
		{
			if(!collect.contains(FinalFeatures1.get(i).trim()))
				collect.add(FinalFeatures1.get(i).trim());
		}
		for(int i = 0; i < FinalFeatures2.size(); i++)
		{
			if(!collect.contains(FinalFeatures2.get(i).trim()))
				collect.add(FinalFeatures2.get(i).trim());
		}
		for(int i = 0; i < FinalFeatures3.size(); i++)
		{
			if(!collect.contains(FinalFeatures3.get(i).trim()))
				collect.add(FinalFeatures3.get(i).trim());
		}
			
			
		File file1 = new File("features.txt");
		 
		// if file doesnt exists, then create it
		if (!file1.exists()) {
			file1.createNewFile();
		}

		FileWriter fw = new FileWriter(file1.getAbsoluteFile());
		BufferedWriter bw1 = new BufferedWriter(fw);
		
		for(int i = 0; i < collect.size(); i++)
			bw1.write(collect.get(i)+"\n");
		bw1.close();
		
		if (bw!= null)
				bw.close();	

	}

}
