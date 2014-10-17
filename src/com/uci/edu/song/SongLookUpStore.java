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

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class SongLookUpStore {

	List<Music2> musicList = new ArrayList<Music2>();
	//Map<String, Integer> mapArtistTerm;
	//Map<String, Integer> mapArtistGenre;
	MongoClient mongoClient;
	DB db;
	private EchoNestAPI en;
	
	// NOT latest version ---> need to check null values of genres and terms and add song!!!
	// I made a mistake during push files to git, the latest files were not commited T-T
	
	public SongLookUpStore() throws EchoNestException, UnknownHostException {
        en = new EchoNestAPI();
        en.setTraceSends(true);
        en.setTraceRecvs(false);
        mongoClient = new MongoClient( "emme.ics.uci.edu" , 27017 );
        //mongoClient = new MongoClient( "localhost" , 27017 );
		db = mongoClient.getDB( "echonest" );
		
        //mapArtistTerm = new HashMap<String, Integer>();
        //mapArtistTerm = getTermsStyleMap();
        //mapArtistGenre = new HashMap<String, Integer>();
        //mapArtistGenre = getGenreMap();
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
					FunctionUtils.writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
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
					FunctionUtils.writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
				} else{
					int id = Integer.parseInt(token[0]);
					if(token[3].isEmpty() || token[4].isEmpty()){
						FunctionUtils.writeToFile(file + "error", "["+token.length+"]" + line + "\n", true);
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
	
	public static void main(String[] args) {
//		SongLookUpStore sl = null; 
//		
//		try {
//			sl = new SongLookUpStore();
//			DBCollection collFind = sl.db.getCollection("all");
//			BasicDBObject query = new BasicDBObject("result", 0);
//			DBCursor cursor = collFind.find(query);
//			int count = 0;
//			try {
//			   while(cursor.hasNext()) {
//			       System.out.println(cursor.next());
//			       count++;
//			   }
//			} finally {
//			   cursor.close();
//			}
//			System.out.println("\n------------------\n" + count);
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		System.exit(0);
		
		// Ing: 49PUSJRUWYLA9RXQN	1-3
		// Jui: U3XEZ8IHU489DLLMD	4-6
		// Ian: NDP4ZK4KOZVBGXJBN	7-9
		int set = 9;
		int start = 0;
		int countFound = 0;
		int countTitleNotFound = 0;
		int countArtistNotFound = 0;
		int i = start;
		String fileName = "data/"+"09010928_SongListened_" + set +".csv";
		String resultFile = "data/"+"09010928_SongListened_JSON_"+ set +"_emme.csv";
		String progress = "data/progress.txt";
		SongLookUpStore lookup = null; 
		
		try {
			lookup = new SongLookUpStore();
			lookup.getTitleFromFile(fileName);
			
			int numSongs = lookup.musicList.size();
			
			System.out.println("set.start: " + set + "." + start + ", end: " + numSongs);
			for(i = start; i < numSongs; i++){
				Music2 m = lookup.musicList.get(i);
				BasicDBObject all = new BasicDBObject();
				
				BasicDBObject input = new BasicDBObject();
				input.append("fileid", m.fileid);
				input.append("datesaved", m.date);
				input.append("posttitle", m.postTitle);
				input.append("artist", m.artist);
				input.append("song", m.songTitle); 
				DBCollection colInput = lookup.db.getCollection("inputs");
				colInput.insert(input);
				all.append("input", input);
				
				int result = -1;	// -1: artist not found, 0: song not found, 1: found
				
				Artist a = lookup.searchSongsByArtist(m.artist);
				if(a != null){
					result++;
					DBCollection colArtist = lookup.db.getCollection("artists");
					colArtist.insert(new BasicDBObject(a.getData()));
					all.append("artist", a.getData());
					Song s = lookup.serchSongsByTitle(m.artist, m.songTitle);
					if(s != null){
						result++;
						DBCollection colSong = lookup.db.getCollection("songs");
						colSong.insert(new BasicDBObject(s.getData()));
						all.append("song", s.getData());
					}
				}
				all.append("result", result);
				
				DBCollection colAll = lookup.db.getCollection("all");
				colAll.insert(all);
	        	System.out.print(i + " ");
				//System.out.println("------------------------------------------------------------");
				//System.out.println(all.toString());
				FunctionUtils.writeToFile(resultFile, all.toString(), true);
				try {
		            Thread.sleep(1500);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
			}
		}
		catch (EchoNestException e) {
			e.printStackTrace();
			String summary = "SUM ["+set+"] Total=" + (i+1) + ", F=" + countFound + ", T=" + countTitleNotFound + ", A=" + countArtistNotFound;
			System.out.println(summary);
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
		    FunctionUtils.writeToFile(progress, ft.format(dNow) + ":" + summary + "\n" , true);
			
		} catch(UnknownHostException e){
			e.printStackTrace();
			String summary = "SUM ["+set+"] Total=" + (i+1) + ", F=" + countFound + ", T=" + countTitleNotFound + ", A=" + countArtistNotFound;
			System.out.println(summary);
			Date dNow = new Date();
		    SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
		    FunctionUtils.writeToFile(progress, ft.format(dNow) + ":" + summary + "\n" , true);
		}
	}
}
