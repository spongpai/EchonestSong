package com.uci.edu.song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.Term;
import com.jEN.SearchSongsExample;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class SongLookUp {
	
	List<Music2> musicList = new ArrayList<Music2>();
	Map<String, Integer> mapArtistTerm;
	Map<String, Integer> mapArtistGenre;
	MongoClient mongoClient;
	DB db;
	private EchoNestAPI en;
	
	public SongLookUp() throws EchoNestException, UnknownHostException {
        en = new EchoNestAPI();
        en.setTraceSends(true);
        en.setTraceRecvs(false);
        mongoClient = new MongoClient( "localhost" , 27017 );
		db = mongoClient.getDB( "echonest" );
		
        mapArtistTerm = new HashMap<String, Integer>();
        mapArtistTerm = getTermsStyleMap();
        mapArtistGenre = new HashMap<String, Integer>();
        mapArtistGenre = getGenreMap();
    }
	
	public void writeToFile(String file, String text, boolean append){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			bw.append(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void getTitleFromFile(String file){
		String line = null;
		BufferedReader br = null;
		try{
			br = new BufferedReader (new InputStreamReader(new FileInputStream(file)));
			line = br.readLine();	// skip the header
			//fileid|datesaved|posttitle|artist|song|ASIN|Note
			while((line = br.readLine())!= null){
				String[] token = line.split("\\|", -1);
				if(token.length != 5){
					writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
				} else{
					int id = Integer.parseInt(token[0]);
					Music2 temp = new Music2(id, token[1], token[2], token[3], token[4]);
					musicList.add(temp);
				}
			}
			br.close();
	    	br = null;
	    } catch (Exception e){
	    	e.printStackTrace();
	    } 
	}
	
	public void getTitleFromFile(String file, int numLine){
		String line = null;
		BufferedReader br = null;
		try{
			br = new BufferedReader (new InputStreamReader(new FileInputStream(file)));
			line = br.readLine();	// skip the header
			//fileid|datesaved|posttitle|artist|songtitle
			int count = 0;
			while((line = br.readLine())!= null && count < numLine){
				String[] token = line.split("\\|", -1);
				if(token.length != 5){
					writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
				} else{
					int id = Integer.parseInt(token[0]);
					if(token[3].isEmpty() || token[4].isEmpty()){
						writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
					} else{
						Music2 temp = new Music2(id, token[1], token[2], token[3], token[4]);
						musicList.add(temp);
					}
				}
				count++;
			}
			br.close();
	    	br = null;
	    } catch (Exception e){
	    	e.printStackTrace();
	    } 
	}
	
	public Map<String, Integer> getTermsStyleMap(){
		DBCollection coll = db.getCollection("artist_terms_style");
		DBCursor cursor = coll.find();
		while(cursor != null && cursor.hasNext()){
			DBObject t = cursor.next();
			mapArtistTerm.put((String) t.get("name"), (int) t.get("i")); 
		}
		return mapArtistTerm;
	}
	
	public Map<String, Integer> getGenreMap(){
		DBCollection coll = db.getCollection("artist_genres");
		DBCursor cursor = coll.find();
		while(cursor != null && cursor.hasNext()){
			DBObject t = cursor.next();
			mapArtistGenre.put((String) t.get("name"), (int) t.get("i")); 
		}
		return mapArtistGenre;
	}
	
	public BasicDBObject artistToDBObject(Artist a){
		BasicDBObject joa = new BasicDBObject();	
   		try {
   			joa.put("id", a.getID());
   	    	joa.put("name", a.getName());
   	    	
   	    	JSONArray bioArr = new JSONArray();
	    	List<Biography> bioList = a.getBiographies();
	    	for(Biography b: bioList)
	    		bioArr.add(b.toJSON());
	    	joa.put("bio", bioArr);
	
	    	joa.put("dis", a.getDiscovery());
	    	joa.put("dis_rank", a.getDiscoveryRank());
	    	
	    	JSONObject dc = new JSONObject();
	    	Map<String, Long> doc = a.getDocCounts();
	    	for(String x: doc.keySet()){
	    		dc.put(x, doc.get(x).intValue());
	    	}
	    	joa.put("doc_count", dc);
	    	
	    	joa.put("fam", a.getFamiliarity());
	    	joa.put("fam_rank", a.getFamiliarityRank());
	    	joa.put("hot", a.getHotttnesss());
	    	joa.put("hot_rank", a.getHotttnesssRank());
	    	joa.put("loc", a.getArtistLocation().toJSON());
	    	
	    	JSONArray termArr = new JSONArray();
	    	List<Term> termList = a.getTerms();
	    	int[] tfArray = new int[mapArtistTerm.size()];
	    	for(Term t: termList){
	    		termArr.add(t.getName());
	    		if(mapArtistTerm.containsKey(t.getName())){
	    			tfArray[mapArtistTerm.get(t.getName())] = 1;
	    		}
	    	}
	    	joa.put("term", termArr);
	    	joa.put("term_tf", tfArray);
	    	
	    	JSONArray genreArr = new JSONArray();
	    	List<String> genreList = a.getGenres();
	    	int[] tfgArray = new int[mapArtistGenre.size()];
	    	for(String g: genreList){
	    		genreArr.add(g);
	    		if(mapArtistGenre.containsKey(g)){
	    			tfgArray[mapArtistGenre.get(g)] = 1;
	    		}
	    	}
	    	joa.put("genre", genreArr);
	    	joa.put("genre_tf", tfgArray);
    	} catch (EchoNestException e) {
			e.printStackTrace();
		}
   		return joa;
	}
	public Artist searchSongsByArtist(String name){
		Artist a = null;
		try{
			ArtistParams ap = new ArtistParams();
			if(name != null && !name.isEmpty())	{
		        ap.addName(name);
		        ap.includeAll();
		        List<Artist> artists = en.searchArtists(ap);
		        if (artists.size() > 0) {
		        	a =  artists.get(0);
		        }
			}
			
		} catch (Exception e){
	    	e.printStackTrace();
	    } 
		return a;
	}
	
	public BasicDBObject songToDBObject(Song s){
		BasicDBObject song = new BasicDBObject();
		
		return song;
	}
	
	public Song serchSongsByTitle(String artist, String title){
		Song s = null;
		if(!artist.isEmpty() && artist != null && !title.isEmpty() && title != null){
			SongParams p = new SongParams();
			
	        p.setArtist(artist);
	        p.setTitle(title);
	        p.setResults(1);	// number of results
	        
	        p.includeAll();
	        
	        p.sortBy("song_hotttnesss", false);
	        
	        try {
				List<Song> songs = en.searchSongs(p);
				if(songs.size() > 0){
					s = songs.get(0);
				}
			} catch (EchoNestException e) {
				e.printStackTrace();
			}
		}
   		return s;
	}
	
	public int searchSongsByTitleArtist(int index, int results)
            throws EchoNestException {
		int result = -1;	// -1: artist not found, 0: artist found, but title not found, 1: song found
		String title = this.musicList.get(index).songTitle;
		String artist = this.musicList.get(index).artist;
		Artist a = this.searchSongsByArtist(artist);
		
		if(a != null){		// artist found
			BasicDBObject artistObj = this.artistToDBObject(a);
			DBCollection colArtist = db.getCollection("artists");
			//colArtist.insert(artistObj);	// insert record to db
			
			Song s = this.serchSongsByTitle(artist, title);
			if(s != null){
				BasicDBObject songObj = this.songToDBObject(s);
				this.musicList.get(index).setSong(s);
				result = 1;				// both song and artist found
			} else{
				result = 0;		// only artist found (song not found)
			}
		}
		return result;
    }
	
	public static void main(String[] args) throws EchoNestException, UnknownHostException{
		int set = 3;
		int start = 56;
		int countFound = 0;
		int countTitleNotFound = 0;
		int countArtistNotFound = 0;
		int i = start;
		SongLookUp lookup = new SongLookUp();
		String fileName = "data/"+"09010928_SongListened_" + set +".csv";
		String resultFile = "data/"+"09010928_SongListened_Info_"+ set +".csv";
		String progress = "data/progress.txt";
		lookup.getTitleFromFile(fileName);
		SearchSongsExample sse;
		
		//String headerShort = "fileid|datesaved|posttitle|artist|searchresult|songtitle|songTitle|songID|artist|duration|";
		String headerFull = "i|fileid|datesaved|posttitle|artist|songtitle(input)|searchresult|songTitle|songID|artist|duration|tempo|mode|" +
    			"s_currency|s_curRank|s_discovery|s_disRank|s_hot|s_hotRank|s_type|" +
    			"a_hot|a_hotRank|a_discovery|a_disRank|a_familiarity|a_famRank|a_location\n";

		//System.out.println(headerFull);
		if(start == 0)
			lookup.writeToFile(resultFile, headerFull, false); 
		else
			lookup.writeToFile(resultFile, headerFull, true); 
		int numSongs = lookup.musicList.size();
		try {
				
			for(i = start; i < numSongs; i++){
				System.out.println();
				int result = lookup.searchSongsByTitleArtist(i, 1);
		        if(result == 1){	//found
		        	System.out.println("FOUND!");
		        	countFound++;
		        	lookup.writeToFile(resultFile, i + "|" + lookup.musicList.get(i).inputToString() + "|" +
		        			result + "|" + lookup.musicList.get(i).getSong().toStringFull() + "\n", true);
		            
		        } else if(result == 0){		// not found song title
		        	
		        	countTitleNotFound++;
		        	System.out.println("song title not found");
		        	lookup.writeToFile(resultFile, i + "|" + lookup.musicList.get(i).inputToString() + "|" + result + "\n", true);
		            
		        } else if(result == -1){	// not found artist
		        	countArtistNotFound++;
		        	System.out.println("artist not found");
		        	lookup.writeToFile(resultFile, i + "|" + lookup.musicList.get(i).inputToString() + "|" + result + "\n", true);
		            
		        }
		        
		        System.out.println(lookup.musicList.get(i).toString());	
		        
		        System.out.println("SUM ["+set+"] Total=" + (i+1) + ", F=" + countFound + ", T=" + countTitleNotFound + ", A=" + countArtistNotFound);
		        try {
		            Thread.sleep(2000);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        
			};
		} catch (EchoNestException e) {
			e.printStackTrace();
			String summary = "SUM ["+set+"] Total=" + (i+1) + ", F=" + countFound + ", T=" + countTitleNotFound + ", A=" + countArtistNotFound;
			System.out.println(summary);
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
			lookup.writeToFile(progress, ft.format(dNow) + ":" + summary + "\n" , true);
			
		}

        
	}
}
