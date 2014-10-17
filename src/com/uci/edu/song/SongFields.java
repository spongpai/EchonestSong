package com.uci.edu.song;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class SongFields {
    public static void main(String[] args){
        try {
//            EchoNestAPI en = new EchoNestAPI();
//            en.setTraceSends(true);
//            en.setTraceRecvs(false);
//            
            MongoClient mongoClient = new MongoClient( "emme.ics.uci.edu" , 27017 );
            DB db = mongoClient.getDB( "echonest" );
            
            // OUTPUT FILES
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
            
            String fOut = "data/output_"+dNow.getTime()+".csv";;
            String fHead = "data/output_"+dNow.getTime()+"_head.csv";;
            String fSummary = "data/output_"+dNow.getTime()+"_summary.csv";
            
                        
            Map<String, Integer> mapArtistTerm = new HashMap<String, Integer>();
            DBCollection coll = db.getCollection("artist_terms");
            DBCursor cursor = coll.find();
            while(cursor != null && cursor.hasNext()){
                DBObject t = cursor.next();
                mapArtistTerm.put((String) t.get("name"), (int) t.get("i")); 
            }
            
            Map<String, Integer> mapArtistGenres = new HashMap<String, Integer>();
            DBCollection collG = db.getCollection("artist_genres");
            DBCursor cursorG = collG.find();
            while(cursorG != null && cursorG.hasNext()){
                DBObject t = cursorG.next();
                mapArtistGenres.put((String) t.get("name"), (int) t.get("i")); 
            }
            
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
            String hInput = "fileid"+sp+"datesaved"+sp+"posttitle"+sp+"artist"+sp+"song"+sp;
            
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
            
            String hArtist = "";
            for(int i = 0; i< aList.size(); i++)
                hArtist += aList.get(i) + sp;
            for(int i = 0; i < mapArtistTerm.size(); i++)
                hArtist += "t"+i + sp;
            for(int i = 0; i < mapArtistGenres.size(); i++)
                hArtist += "g"+i + sp;
            
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
            
            
            // Retrieve song
            DBCollection coll_artist = db.getCollection("all");
            DBCursor cursor1 = coll_artist.find();
            while(cursor1 != null && cursor1.hasNext()){
                DBObject obj = cursor1.next();
                DBObject input = (DBObject) obj.get("input");
                DBObject a = (DBObject) obj.get("artist");
                DBObject s = (DBObject) obj.get("song");
                
                String aInput = input.get("fileid") + sp + input.get("datesaved") + sp + input.get("posttitle")
                        + sp + input.get("artist") + sp + input.get("song") + sp;
                
                StringBuilder sba = new StringBuilder();    // string builder artist
                StringBuilder sbs = new StringBuilder();    // string builder song
                int cInput = 0, cArtist = 0, cSong = 0;
                cInput++;
                if(a != null){
                    cArtist++;
                    for(String key: aList){
                        if(key.contains(":")){
                            String[] keys = key.split(":");
                            sba.append(((BSONObject)a.get(keys[0])).get(keys[1]) + sp);
                        } else{
                            sba.append(a.get(key) + sp);
                        }
                    }
                    
                    int[] aTerms = new int[mapArtistTerm.size()];
                    BasicDBList e = (BasicDBList) a.get("terms");
                    for(int i = 0; i < e.size(); i++){
                        String t = (String) ((DBObject) e.get(i)).get("name");
                        int index = mapArtistTerm.get(t);
                        if(index >= 0 && index < aTerms.length){
                            aTerms[index] = 1;
                            countTerm[index]++;
                        }
                    }
                    String termsStr = Arrays.toString(aTerms);
                    termsStr = termsStr.substring(1, termsStr.length()-1).replace(" ", "").replace(",", sp);
                    sba.append(termsStr + sp);
                    
                    int[] aGenres = new int[mapArtistGenres.size()];
                    BasicDBList g = (BasicDBList) a.get("genres");
                    for(int i = 0; i < g.size(); i++){
                        String t = (String) ((DBObject) g.get(i)).get("name");
                        int index = mapArtistGenres.get(t);
                        if(index >= 0 && index < aGenres.length){
                            aGenres[index] = 1;
                            countGenres[index]++;
                        }
                    }
                    String genresStr = Arrays.toString(aGenres);
                    genresStr = genresStr.substring(1, genresStr.length()-1).replace(" ", "").replace(",", sp);
                    
                    sba.append(genresStr + sp);
                    
                    if(s != null){
                        cSong++;
                        for(String key: sList){
                            if(key.contains(":")){
                                String[] keys = key.split(":");
                                sbs.append(((BSONObject)s.get(keys[0])).get(keys[1]) + sp);
                            } else{
                                sbs.append(s.get(key) + sp);
                            }
                        }
                        int[] sTypes = new int[mapSongTypes.size()];    
                        BasicDBList st = (BasicDBList) s.get("song_type");
                        for(int i = 0; i < st.size(); i++){
                            String t = (String) st.get(i);
                            int index = mapSongTypes.get(t);
                            if(index >= 0 && index < sTypes.length)
                                sTypes[index] = 1;
                        }
                        String typeStr = Arrays.toString(sTypes);
                        typeStr = typeStr.substring(1, typeStr.length()-1).replace(" ", "").replace(",", sp);
                        sbs.append(typeStr + sp);
                    }
                }
                
                System.out.println(hInput + hArtist + hSong + nl);
                System.out.println(aInput + sba.toString() + sbs.toString() + nl);
                
                
                FunctionUtils.writeToFile(fOut, aInput + sba.toString() + sbs.toString() + nl, true);
            }    
            
            
            
            
            
            
            String summary = ft.format(dNow) + nl + "non-zero terms: ";
            for(int i = 0; i < countTerm.length; i++)
                if(countTerm[i] > 0)
                    summary += i + "["+countTerm[i]+"], ";
            summary += nl + "non-zero genres: ";
            for(int i = 0; i < countGenres.length; i++)
                if(countGenres[i] > 0)
                    summary += i + "["+countGenres[i]+"], ";
                        
            FunctionUtils.writeToFile(fHead, hInput + hArtist + hSong + nl, false);
            FunctionUtils.writeToFile(fSummary, summary + nl, false);
            
//            DBCursor cursor1 = coll_artist.findOne();
//            while(cursor1 != null && cursor1.hasNext()){
//                DBObject t = cursor1.next();
//                t.get("song.song_discovery_rank");
//                System.out.println(t.get("song").toString());
//            }
            System.exit(0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


