package com.yelpdata.ir.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import org.apache.poi.ss.usermodel.Cell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

class ValueComparatorDouble implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparatorDouble(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

public class ReviewPredict {

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
	
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
		{
		    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
		    {
		        @Override
		        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
		        {
		            return (o1.getValue()).compareTo( o2.getValue() );
		        }
		    } );
		
		    Map<K, V> result = new LinkedHashMap<K, V>();
		    for (Map.Entry<K, V> entry : list)
		    {
		        result.put( entry.getKey(), entry.getValue() );
		    }
		    return result;
		}
    
	public static String getTop(HashMap<String,Integer> map, int limit){
		
		  ValueComparator bvc =  new ValueComparator(map);
	      TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
	      String topWords = "";
	      
	      sorted_map.putAll(map);
	      
		//HashMap<String, Integer> sorted = new HashMap<String,Integer>();
		 //System.out.println(sortByValue(map).toString());	
	     // System.out.println(map.toString());
		//sorted = (HashMap<String, Integer>) sortByValue(map);
			//System.out.println(sorted_map.toString());
	      Iterator it = sorted_map.entrySet().iterator();
	      int count = 1;
		    while (it.hasNext()) {
		    	Map.Entry keyVal = (Map.Entry)it.next();
		    	if(count < limit){
		    		//System.out.println(keyVal.getKey().toString()+"   "+keyVal.getValue().toString());
		    		topWords = topWords + " " + keyVal.getKey().toString();
		    	}
		    	else
		    		break;
		    	count++;
		    }
	      return topWords;
	}
	
	public static void readReviews() throws Exception
	{
		String path = "tp.xls";

		File fout1 = new File("FinalBigrams.txt");
		FileWriter fileWriter1 = new FileWriter(fout1,false);	
		
		FileInputStream file = new FileInputStream(new File(path));
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		HashMap<String, Integer> negCount = new HashMap<String, Integer>();
		HashMap<String, Integer> unigramCount = new HashMap<String, Integer>();
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		
		HSSFWorkbook clubCatData = new HSSFWorkbook();
		
		HashMap<String,String> mapCatTextFinal = new HashMap<String,String>();
		File fout = new File("processInput.txt");
		FileWriter fileWriter = new FileWriter(fout,false);	
		BufferedWriter bw = null;
		while(true)
		{	
			
			row = sheet.getRow(counter);
			
			if(row != null)
			{
				int stars = (int)row.getCell(0).getNumericCellValue();
				String text = row.getCell(1).getStringCellValue();
			
				String removed = removeStopWords(text);
				//String removed = text;
				counter++;
				//if (counter == 100)
					//break;
				 bw = new BufferedWriter(fileWriter);
				 
				fileWriter.append(removed);
				//bw.write(removed);
			
			}
			else
				break;	
		}
		if (bw!= null)
				bw.close();	

		Process p0 = Runtime.getRuntime().exec("python UnigramScript.py processInput.txt");
		System.out.println("before0");
		p0.waitFor();
		System.out.println("after0");
		
		//-------------------------------------------------------------------	
		/*
		Process p = Runtime.getRuntime().exec("python script.py processInput.txt");
		System.out.println("before");
		p.waitFor();
		System.out.println("after");
		
		//-------------------------------------------------------------------		
		FileReader fr = new FileReader("output.txt");
		BufferedReader br = new BufferedReader(fr);
		String currentLine = br.readLine();
		
		while ((currentLine = br.readLine()) != null)
	    	if(wordCount.containsKey(currentLine)){
				wordCount.put(currentLine, wordCount.get(currentLine)+1);
			}
			else{
				wordCount.put(currentLine, 1);
			}
    	fr.close();
    	String bigrams =  getTop(wordCount, 501);
    	
		System.out.println(bigrams);
		fileWriter1.append(bigrams);
		Process p1 = Runtime.getRuntime().exec("python scriptNegative.py processInput.txt");
		System.out.println("before2");
		p1.waitFor();
		System.out.println("after2");
		
		//-------------------------------------------------------------------
		FileReader fr1 = new FileReader("outputNegatives.txt");
		BufferedReader br1 = new BufferedReader(fr1	);
		String currentLine1 = br1.readLine();
		
		while ((currentLine1 = br1.readLine()) != null)
	    	if(negCount.containsKey(currentLine1)){
	    		negCount.put(currentLine1, negCount.get(currentLine1)+1);
			}
			else{
				negCount.put(currentLine1, 1);
			}
    	fr1.close();
    	String negatives =  getTop(negCount, 101);
		System.out.println(negatives);
		fileWriter1.append(negatives);
		fileWriter1.close();
		*/
		
		
	}
	public static void main(String args[]) throws Exception
	{
		readReviews();
	}
}