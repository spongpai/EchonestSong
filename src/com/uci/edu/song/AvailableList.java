package com.uci.edu.song;

import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.echonest.api.v4.Biography;
import com.echonest.api.v4.Blog;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestAPI.TermType;
import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.Term;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class AvailableList {

	public AvailableList()  {
        
    }
	
	
	
	public static void main(String[] args){
		try {
			EchoNestAPI en = new EchoNestAPI();
	        en.setTraceSends(true);
	        en.setTraceRecvs(false);
	        
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "echonest" );
			
			// Retrieve song
//			DBCollection coll_artist = db.getCollection("songs");
//			DBCursor cursor1 = coll_artist.find();
//			while(cursor1 != null && cursor1.hasNext()){
//				DBObject t = cursor1.next();
//				t.get("song.song_discovery_rank");
//				System.out.println(t.get("song").toString());
//			}
//			System.exit(0);
			
			Map<String, Integer> mapArtistTerm = new HashMap<String, Integer>();
			DBCollection coll = db.getCollection("artist_terms_style");
			DBCursor cursor = coll.find();
			while(cursor != null && cursor.hasNext()){
				DBObject t = cursor.next();
				mapArtistTerm.put((String) t.get("name"), (Integer) t.get("i")); 
				
			}
			
			
//			for(String key: mapArtistTerm.keySet()){
//				System.out.println(key + ", i: " + mapArtistTerm.get(key));
//			}
			
			BasicDBObject all = new BasicDBObject();
			
			 // get Artist Info 
	        String artist = "Radiohead";
			ArtistParams ap = new ArtistParams();
			 try {
				 if(artist != null || !artist.isEmpty())	{
			        ap.addName(artist);
			        ap.includeAll();
			        List<Artist> artists = en.searchArtists(ap);
			        if (artists.size() > 0) { 
			        	BasicDBObject art = new BasicDBObject();
			        	art.put("artist", artists.get(0).getData());
			        	DBCollection colArtists = db.getCollection("artists");
			        	colArtists.insert(art);
			        	all.append("artist", artists.get(0).getData());
						System.out.println(art.toString());
			        }
				 }
			} catch (EchoNestException e) {
				e.printStackTrace();
			}

			 
			// get song info
			String title = "Karma Police";
			Song s = null;
			SongParams p = new SongParams();
	        p.setTitle(title);
	        p.setResults(1);	// number of results
	        
	        p.includeAll();
	        
	        p.sortBy("song_hotttnesss", false);
	        
	        try {
				List<Song> songs = en.searchSongs(p);
				if(songs.size() > 0){
					s = songs.get(0);
					BasicDBObject jos = new BasicDBObject();
		        	jos.put("song", s.getData());
		        	DBCollection colSongs = db.getCollection("songs");
		        	colSongs.insert(jos);
		        	all.append("song", s.getData());
					System.out.println(s.toString());
				}
			} catch (EchoNestException e) {
				e.printStackTrace();
			}
	        
	        DBCollection colSongs = db.getCollection("all");
        	colSongs.insert(all);
        	System.out.println(all.toString());
        	
	        // get both info
			 
			
			// get artist info
			/*
			String artist = "Radiohead";
			ArtistParams ap = new ArtistParams();
			if(artist != null || !artist.isEmpty())	{
		        ap.addName(artist);
		        ap.includeAll();
		        List<Artist> artists = en.searchArtists(ap);
		        if (artists.size() > 0) { 
		        	Artist a = artists.get(0);
		        	BasicDBObject joa = new BasicDBObject();
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
		        	DBCollection colArtist = db.getCollection("artists");
					colArtist.insert(joa);
			        
		        	
	//	        	for(int i = 0; i < termList.size(); i++){
	//	        		System.out.println(mapArtistTerm.get(termList.get(i).getName()) + ":" + termList.get(i).getName());
	//	        	}
	//	        	
	//	        	System.out.println("count terms: " + termList.size());
					
					System.out.println("\n" + joa.toString());
		        }
	        
			}
			
			*/
			 
			/*
			// get all available terms (type = MOOD)
			List<String> terms = en.listTerms(TermType.MOOD);
			System.out.println(terms.size());
			DBCollection coll = db.getCollection("artist_terms_mood");
			for(int i = 0; i < terms.size(); i++){
				BasicDBObject doc = new BasicDBObject("name", terms.get(i)).append("i", i);
				//coll.insert(doc);
			}
			*/
			
			/*
			// get all available terms (type = STYLE)
			List<String> terms2 = en.listTerms(TermType.MOOD);
			System.out.println(terms2.size());
			DBCollection coll2 = db.getCollection("artist_terms_mood");
			for(int i = 0; i < terms2.size(); i++){
				BasicDBObject doc = new BasicDBObject("name", terms2.get(i)).append("i", i);
				//coll2.insert(doc);
			}
			*/
			
			/*
			// get all available genres
			List<String> genres = en.listGenres();
			System.out.println(genres.size());
			DBCollection coll3 = db.getCollection("artist_genres");
			for(int i = 0; i < genres.size(); i++){
				BasicDBObject doc = new BasicDBObject("name", genres.get(i)).append("i", i);
				coll3.insert(doc);
			}
			*/
			
		} catch (EchoNestException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	
	}
}
