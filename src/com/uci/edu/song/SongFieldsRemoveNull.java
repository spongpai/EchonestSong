package com.uci.edu.song;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestAPI.TermType;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class SongFieldsRemoveNull {
    public static void main(String[] args){
        try {
//            EchoNestAPI en = new EchoNestAPI();
//            en.setTraceSends(true);
//            en.setTraceRecvs(false);
//            
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
            DB db = mongoClient.getDB( "echonest" );
            
            // OUTPUT FILES
            String colExt = "complete2";
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
            
            
            // set output files name
            String fOut = "data/output_" + colExt + "_" + dNow.getTime() + ".csv";
            String fHead = "data/output_" + colExt + "_" + dNow.getTime() +"_head.csv";;
            String fTerm = "data/output_" + colExt + "_" + dNow.getTime() +"_term.csv";;
            String fGenre = "data/output_" + colExt + "_" + dNow.getTime() +"_genre.csv";;
            String fSummary = "data/output_" + colExt + "_" + dNow.getTime() +"_summary.csv";
            
            int countSongs = 0;
                    
            // Create terms map from database
            Map<String, Integer> mapArtistTerm = new HashMap<String, Integer>();
            DBCollection coll = db.getCollection("artist_terms");
            DBCursor cursor = coll.find();
            while(cursor != null && cursor.hasNext()){
                DBObject t = cursor.next();
                mapArtistTerm.put((String) t.get("name"), (Integer) t.get("i")); 
            }
            //ArrayList<Integer> nonZeroTerms = new ArrayList<Integer>();
            //ArrayList<Integer> nonZeroGenres = new ArrayList<Integer>();
            
            // Create genres map from database
            Map<String, Integer> mapArtistGenres = new HashMap<String, Integer>();
            DBCollection collG = db.getCollection("artist_genres");
            DBCursor cursorG = collG.find();
            while(cursorG != null && cursorG.hasNext()){
                DBObject t = cursorG.next();
                mapArtistGenres.put((String) t.get("name"), (Integer) t.get("i")); 
            }
            
            // create arrayList of song that store terms and genres features
            ArrayList<int[]> songListTerms = new ArrayList<int[]>();
            ArrayList<int[]> songListGenres = new ArrayList<int[]>();
            ArrayList<String> songListID = new ArrayList<String>();
            ArrayList<String> songListSSN = new ArrayList<String>();
            
            //HashMap<String, int[]> songListTerms = new HashMap<String, int[]>();
            //HashMap<String, int[]> songListGenres = new HashMap<String, int[]>();
            
            int[] countTerm = new int[mapArtistTerm.size()];
            int[] countGenres = new int[mapArtistGenres.size()];
             
            Map<String, Integer> mapSongTypes = new HashMap<String, Integer>();
            mapSongTypes.put("christmas", 0);
            mapSongTypes.put("live", 1);
            mapSongTypes.put("studio", 2);
            mapSongTypes.put("acoustic", 3);
            mapSongTypes.put("electric", 4);
            
            String sp = "|*|";
            String nl = "\n";
            
            // String Input
            String hInput = "fileid"+sp+"datesaved"+sp+"posttitle"+sp+"artist"+sp+"song"+sp+"result"+ sp;
            
            // String Artist
            List<String> aList = new ArrayList<String>();
            aList.add("id");
            aList.add("name");
            aList.add("discovery");
            aList.add("discovery_rank");
            aList.add("familiarity");
            aList.add("familiarity_rank");
            aList.add("hotttnesss");
            aList.add("hotttnesss_rank");
            aList.add("doc_counts:reviews");
            aList.add("doc_counts:audio");
            aList.add("doc_counts:news");
            aList.add("doc_counts:songs");
            aList.add("doc_counts:images");
            aList.add("doc_counts:biographies");
            aList.add("doc_counts:video");
            aList.add("doc_counts:blogs");
            aList.add("artist_location:region");
            aList.add("artist_location:location");
            aList.add("artist_location:country");
            aList.add("artist_location:city");
            int countNonZeroTerms = 0, countNonZerogenres = 0, countZeroTerms = 0, countZeroGenres = 0;
            String hArtist = "";
            for(int i = 0; i< aList.size(); i++)
                hArtist += aList.get(i) + sp;
            
            List<String> sList = new ArrayList<String>();
            sList.add("id");
            sList.add("title");
            sList.add("song_discovery");
            sList.add("song_currency");
            sList.add("artist_id");
            sList.add("song_hotttnesss");
            sList.add("audio_summary:instrumentalness");
            sList.add("audio_summary:speechiness");
            sList.add("audio_summary:tempo");
            sList.add("audio_summary:danceability");
            sList.add("audio_summary:acousticness");
            sList.add("audio_summary:audio_md5");
            sList.add("audio_summary:liveness");
            sList.add("audio_summary:mode");
            sList.add("audio_summary:duration");
            sList.add("audio_summary:valence");
            sList.add("audio_summary:loudness");
            sList.add("audio_summary:time_signature");
            sList.add("audio_summary:key");
            sList.add("audio_summary:energy");
            String hSong = "";
            for(int i = 0; i< sList.size(); i++)
                hSong += sList.get(i) + sp;
            for(String k: mapSongTypes.keySet())
                hSong += "type:" + k + sp;
            
            int format = 2;
            String ssn = "";
            if(format == 2){
            	ssn = "SSNumber" + sp;
            }
            
            // Retrieve song
            DBCollection coll_artist = db.getCollection("all_" +colExt);
            DBCursor cursor1 = coll_artist.find();
            int cInput = 0, cArtist = 0, cSong = 0;
            
            FunctionUtils.writeToFile(fOut, ssn + hInput + hArtist + hSong + nl, false);
            
            while(cursor1 != null && cursor1.hasNext()){
            	countSongs++;
                DBObject obj = cursor1.next();
                DBObject input = (DBObject) obj.get("input");
                DBObject a = (DBObject) obj.get("artist");
                DBObject s = (DBObject) obj.get("song");
                
                String aInput = input.get("ssnumber") + sp + input.get("fileid") + sp + input.get("datesaved") + sp + input.get("posttitle")
                        + sp + input.get("artist") + sp + input.get("song") + sp + obj.get("result") + sp;
                
                StringBuilder sba = new StringBuilder();    // string builder artist
                StringBuilder sbs = new StringBuilder();    // string builder song
                songListID.add(cInput, String.valueOf(input.get("fileid")));
                songListSSN.add(cInput,String.valueOf(input.get("ssnumber")));
                int[] aGenres = new int[mapArtistGenres.size()];
                int[] aTerms = new int[mapArtistTerm.size()];
                songListTerms.add(cInput, aTerms);
                songListGenres.add(cInput, aGenres);
                																									
                
                if(a != null){
                    cArtist++;
                    for(String key: aList){
                        if(key.contains(":")){
                            String[] keys = key.split(":");
                            if(a.get(keys[0]) != null){
                            	sba.append(((BSONObject)a.get(keys[0])).get(keys[1]) + sp);
                            } else{
                            	sba.append("null" + sp);
                            }
                        } else{
                            sba.append(a.get(key) + sp);
                        }
                    }
                    
                    // Count terms
                    BasicDBList e = (BasicDBList) a.get("terms");
                    if(e != null) {
	                    for(int i = 0; i < e.size(); i++){
	                    	DBObject tt = (DBObject) e.get(i);
	                    	if(tt != null){
		                        String t = (String) tt.get("name");
		                        int index = mapArtistTerm.get(t);
		                        if(index >= 0 && index < aTerms.length){
		                            aTerms[index] = 1;
		                            countTerm[index]++;
		                        }
	                    	}
	                    }
                    }
                    // update aTerms array
                    songListTerms.add(cInput, aTerms);
                    //songListTerms.put((String) input.get("fileid"), aTerms);
                    
                    // Count genres
                     BasicDBList g = (BasicDBList) a.get("genres");
                    if(g != null) {
	                    for(int i = 0; i < g.size(); i++){
	                        String t = (String) ((DBObject) g.get(i)).get("name");
	                        if(mapArtistGenres.get(t) != null){
		                        int index = mapArtistGenres.get(t);
		                        if(index >= 0 && index < aGenres.length){
		                            aGenres[index] = 1;
		                            countGenres[index]++;
		                        }
	                        }
	                    }
                    }
                    // update aGenres array
                    songListGenres.add(cInput, aGenres);
                    //songListGenres.put((String) input.get("fileid"), aGenres);
                    
                    
                    if(s != null){
                        cSong++;
                        for(String key: sList){
                            if(key.contains(":")){
                                String[] keys = key.split(":");
                                if(s.get(keys[0]) != null){
                                	sbs.append(((BSONObject)s.get(keys[0])).get(keys[1]) + sp);
                                } else{
                                	sbs.append("null" + sp);
                                }
                            } else{
                                sbs.append(s.get(key) + sp);
                            }
                        }
                        int[] sTypes = new int[mapSongTypes.size()];    
                        BasicDBList st = (BasicDBList) s.get("song_type");
                        for(int i = 0; i < st.size(); i++){
                            String t = (String) st.get(i);
                            if(mapSongTypes.get(t) != null){
	                            int index = mapSongTypes.get(t);
	                            if(index >= 0 && index < sTypes.length)
	                                sTypes[index] = 1;
                            }
                        }
                        String typeStr = Arrays.toString(sTypes);
                        typeStr = typeStr.substring(1, typeStr.length()-1).replace(" ", "").replace(",", sp);
                        sbs.append(typeStr + sp);
                    }
                }
                
                FunctionUtils.writeToFile(fOut, aInput + sba.toString() + sbs.toString() + nl, true);
                System.out.println("write to file " + countSongs + ", result " + obj.get("result"));
                cInput++;
                
            }    
            
            // non zero terms header
            StringBuilder nzTermsHeader = new StringBuilder();
            nzTermsHeader.append(ssn + "fieldID" + sp);
            for(int i = 0; i < mapArtistTerm.size(); i++){
            	if(countTerm[i] != 0){
            		nzTermsHeader.append("t" + i + sp);
            	}
            }
            nzTermsHeader.append(nl);
            
            // non zero genres header
            StringBuilder nzGenresHeader = new StringBuilder();
            nzGenresHeader.append(ssn + "fieldID" + sp);
            for(int i = 0; i < mapArtistGenres.size(); i++){
            	if(countGenres[i] != 0){
            		nzGenresHeader.append("g" + i + sp);
            	}
            }
            nzGenresHeader.append(nl);
            
            // Generate terms and genres features and write to the files
            StringBuilder termsStr = new StringBuilder();
            StringBuilder genresStr = new StringBuilder();
            for(int i = 0; i < songListID.size(); i++){
            	
            	// create (non-zero) terms string
            	System.out.println( songListSSN.get(i) + sp + songListID.get(i)  + sp);
            	
            	if(format == 2){
            		termsStr.append(songListSSN.get(i) + sp);
            		genresStr.append(songListSSN.get(i) + sp);
            	}
            	
            	termsStr.append(songListID.get(i) + sp);
            	int[] temp = songListTerms.get(i);
            	for(int j = 0; j < temp.length; j++){
            		if(countTerm[j] != 0)
            			termsStr.append(temp[j] + sp);
            	}
            	termsStr.append(nl);
            	
            	// create (non-zero) genres string
            	if(format == 2)
            		
            	genresStr.append(songListID.get(i) + sp);
            	int[] tempG = songListGenres.get(i);
            	for(int j = 0; j < tempG.length; j++){
            		if(countGenres[j] != 0)
            			genresStr.append(tempG[j] + sp);
            	}
            	genresStr.append(nl);
            }
            
            // write terms and genres feature into a new file
            FunctionUtils.writeToFile(fTerm, nzTermsHeader.toString() + termsStr.toString(), false);
            FunctionUtils.writeToFile(fGenre, nzGenresHeader.toString() + genresStr.toString(), false);
           
           
            String summary = ft.format(dNow) + nl + "non-zero terms ("+countNonZeroTerms+"): ";
            for(int i = 0; i < countTerm.length; i++)
                if(countTerm[i] > 0)
                    summary += i + "["+countTerm[i]+"], ";
            summary += nl + "non-zero genres ("+countNonZerogenres+"): ";
            for(int i = 0; i < countGenres.length; i++)
                if(countGenres[i] > 0)
                    summary += i + "["+countGenres[i]+"], ";
            
            summary += nl + "non-zero terms (" + countZeroTerms + "): " + nzTermsHeader.toString() 
            		+ nl + "non-zero genres (" + countZeroGenres + "): " + nzGenresHeader.toString() 
            		+ nl + "total songs input = " + cInput
            		+ nl + "artist found = " + cArtist
            		+ nl + "both artist and song found = " + cSong;
            
            
            FunctionUtils.writeToFile(fSummary, summary + nl, false);
            
            System.out.println(summary);
            System.exit(0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


