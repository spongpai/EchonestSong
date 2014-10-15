package com.echonest.api.v4;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.echonest.api.v4.util.Commander;

public class Song extends ENItem {

    /** 
     * Possible song types
     */
    public enum SongType {

        christmas, live, studio
    }

    /**
     * Possible states for song type
     */
    public enum SongTypeFlag {

        True, False, seed, any
    }
    
    private final static String PATH = "songs[0]";
    private Map<String, Track> trackMap = new HashMap<String, Track>();
    private TrackAnalysis analysis = null;

    @SuppressWarnings("unchecked")
    Song(EchoNestAPI en, Map map) throws EchoNestException {
        super(en, "song", PATH, map);
    }

    public Song(EchoNestAPI en, String id) throws EchoNestException {
        super(en, "song", PATH, id);
    }
    
    public Map getSongChar(){
    	Map<String, Object> sChar = new HashMap<String, Object>();
    	try {
			sChar.put("song_discovery_rank", this.getSongDiscoveryRank());
		
	    	sChar.put("song_discovery", this.getSongDiscovery());
	    	sChar.put("song_currency_rank", this.getSongCurrencyRank());
	    	sChar.put("song_currency", this.getSongCurrency());
	//    	sChar.put("audio_instrumentalness", this.g());
	//    	sChar.put("",);
	//    	sChar.put("",);
	//    	sChar.put("",);
	//    	sChar.put("",);
	//    	sChar.put("",);
	//    	sChar.put("",);
    	} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return sChar;
    }

    public Map getData(){
    	return data;
    }
    @Override
    public String toString() {
        return data.toString();
    }

    public String getTitle() {
        return getString("title");
    }

    public String getArtistName() {
        return getString("artist_name");
    }

    
    public String getID7DigitalUS() {
        return getString("id:7digital-US");
    }
    
    //TBD - switch to release name when we have it
    public String getReleaseName() {
        return getTitle();
        // return getString("release_name");
    }

    public String getArtistID() {
        return getString("artist_id");
    }

    public String getAudio() {
        return getString("audio");
    }

    public String getCoverArt() {
        return getReleaseImage();
    }

    public String getReleaseImage() {
        return getString("release_image");
    }

    /*
     *  Song buckets: artist data 
     */
    public Location getArtistLocation() throws EchoNestException {
        fetchBucket("artist_location");
        Double latitude = getDouble("artist_location.latitude");
        Double longitude = getDouble("artist_location.longitude");
        String placeName = getString("artist_location.location");
        return new Location(latitude, longitude, placeName);
    }

    public double getArtistDiscovery() throws EchoNestException {
    	//fetchBucket("artist_discovery");
    	return getDouble("artist_discovery");
    }
    
    public double getArtistDiscoveryRank() throws EchoNestException {
    	//fetchBucket("artist_discovery_rank");
    	return getLong("artist_discovery_rank");
    }
    

    public double getArtistHotttnesss() throws EchoNestException {
        //fetchBucket("artist_hotttnesss");
        return getDouble("artist_hotttnesss");
    }
    
    public double getArtistHotttnesssRank() throws EchoNestException {
        //fetchBucket("artist_hotttnesss_rank");
        return getLong("artist_hotttnesss_rank");
    }

    public double getArtistFamiliarity() throws EchoNestException {
        //fetchBucket("artist_familiarity");
        return getDouble("artist_familiarity");
    }
    
    public double getArtistFamiliarityRank() throws EchoNestException {
        //fetchBucket("artist_familiarity_rank");
        return getInteger("artist_familiarity_rank");
    }
    
    /*
     *  Song buckets: song data 
     */
    public double getSongCurrency() throws EchoNestException {
        //fetchBucket("song_currency");
        return getDouble("song_currency");
    }
    
    public double getSongCurrencyRank() throws EchoNestException {
        //fetchBucket("song_currency_rank");
        return getInteger("song_currency_rank");
    }
    
    public double getSongDiscovery() throws EchoNestException {
        //fetchBucket("song_discovery");
        return getDouble("song_discovery");
    }

    
    public double getSongDiscoveryRank() throws EchoNestException {
        //fetchBucket("song_discovery_rank");
        return getInteger("song_discovery_rank");
    }

    public double getSongHotttnesss() throws EchoNestException {
        //fetchBucket("song_hotttnesss");
        return getDouble("song_hotttnesss");
    }
    
    public double getSongHotttnesssRank() throws EchoNestException {
        //fetchBucket("song_hotttnesss_rank");
        return getInteger("song_hotttnesss_rank");
    }
    
    // returns a list of song types for the song. Possible song types returned are: 'christmas', 'live' 'studio', 'acoustic', and 'electric'
    public String getSongType() throws EchoNestException {
        fetchBucket("song_type");
        JSONArray typeArr =  (JSONArray) getObject("song_type");
        String typeStr = "";
        for(int i =0; i< typeArr.size(); i++){
        	typeStr += typeArr.get(i) +"|";
        }
        return typeStr;
    }
    
    /*
     *  Song buckets: audio data 
     */
    public double getDuration() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.duration");
    }

    public double getLoudness() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.loudness");
    }

    public double getTempo() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.tempo");
    }

    public double getEnergy() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.energy");
    }

    public double getDanceability() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.danceability");
    }

    public String getAnalysisURL() throws EchoNestException {
        fetchBucket("audio_summary");
        return getString("audio_summary.analysis_url");
    }

    public double getInstrumentalness() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.instrumentalness");
    }
    
    @SuppressWarnings("unchecked")
    public TrackAnalysis getAnalysis() throws EchoNestException {
        try {
            if (analysis == null) {
                Map analysisMap = Commander.fetchURLAsJSON(getAnalysisURL());
                analysis = new TrackAnalysis(analysisMap);
            }
        } catch (IOException e) {
            throw new EchoNestException(e);
        }
        return analysis;
    }

    public int getTimeSignature() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.time_signature");
    }

    public int getMode() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.mode");
    }

    public int getKey() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.key");
    }

    @SuppressWarnings("unchecked")
    public Track getTrackOld(String idSpace) throws EchoNestException {
        Track track = trackMap.get(idSpace);
        if (track == null) {
            // see if we already have the track data
            List tlist = (List) getObject("tracks");
            if (tlist == null) {
                String[] buckets = {"tracks", "id:" + idSpace};
                fetchBuckets(buckets, true);
                tlist = (List) getObject("tracks");
            }
            for (int i = 0; tlist != null && i < tlist.size(); i++) {
                Map tmap = (Map) tlist.get(i);
                String tidSpace = (String) tmap.get("catalog");
                if (idSpace.equals(tidSpace)) {
                    track = new Track(en, tmap);
                    trackMap.put(idSpace, track);
                }
            }
        }
        return track;
    }

    /**
     * Gets a track for the given idspace
     *
     * @param idSpace the idspace of interest
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Track getTrackOlder(String idSpace) throws EchoNestException {
        Track track = trackMap.get(idSpace);
        if (track == null) {
            // nope, so go grab it.
            String[] buckets = {"tracks", "id:" + idSpace};
            fetchBuckets(buckets, true);
            List tlist = (List) getObject("tracks");
            if (tlist != null) {
                for (Object item : tlist) {
                    Map tracks = (Map) item;
                    String catalog = (String) tracks.get("catalog");
                    String trid = (String) tracks.get("id");
                    if (!trackMap.containsKey(catalog)) {
                        track = en.newTrackByID(trid);
                        trackMap.put(catalog, track);
                    }
                }
            }
        }
        return track;
    }



    @SuppressWarnings("unchecked")
    public Track getTrack(String idSpace) throws EchoNestException {
        Track track = trackMap.get(idSpace);
        if (track == null) {
            List tlist = (List) getObject("tracks");
            if (tlist != null) {
                for (Object item : tlist) {
                    Map trackData = (Map) item;
                    String catalog = (String) trackData.get("catalog");
                    String trid = (String) trackData.get("id");
                    if (!trackMap.containsKey(catalog)) {
                        Track ttrack = Track.createTrackFromSong(en, trackData);
                        trackMap.put(catalog, ttrack);
                    }
                }
                track = trackMap.get(idSpace);
            }
        }
        return track;
    }
    
    

    public void showAll() throws EchoNestException {
        String[] buckets = {"audio_summary", "song_hotttnesss",
            "artist_hotttnesss", "artist_familiarity", "artist_location"};

        fetchBuckets(buckets);
        System.out.println("Title      : " + getTitle());
        System.out.println("ID         : " + getID());

        System.out.println("Artist     : " + getArtistName());
        System.out.println("ArtistID   : " + getArtistID());
        System.out.println("Location   : " + getArtistLocation());
        System.out.println("Familiarity: " + getArtistFamiliarity());
        System.out.println("Hotttnesss : " + getArtistHotttnesss());
        System.out.println("Duration   : " + getDuration());
        System.out.println("Key        : " + getKey());
        System.out.println("Loudness   : " + getLoudness());
        System.out.println("SHotttnesss: " + getSongHotttnesss());
        System.out.println("Tempo      : " + getTempo());
        System.out.println("Danceability: " + getDanceability());
        System.out.println("Energy     : " + getEnergy());

        System.out.println("TimeSig    : " + getTimeSignature());
        System.out.println();
    }
    
    public String getParamsSetHeaderShort(){
    	return "songTitle|songID|artist|duration|";
    }
    
    public String toStringShort(){
    	String songStr = "";
    	try {
			songStr = this.getTitle() + "|" + this.getID() + "|" +
					this.getArtistName() + "|" + this.getDuration() ;
    	} catch (EchoNestException e) {
			e.printStackTrace();
		}
    	return songStr;
    }
    
    public String getParamsSetHeaderFull(){
    	return "songTitle|songID|artist|duration|tempo|mode|" +
    			"s_currency|s_curRank|s_discovery|s_disRank|s_hot|s_hotRank|s_type|" +
    			"a_hot|a_hotRank|a_discovery|a_disRank|a_familiarity|a_famRank|a_location";
    }
    public String toStringFull(){
    	String songStr = "";
    	try {
			songStr = this.getTitle() + "|" + this.getID() + "|" +
					this.getArtistName() + "|" + this.getDuration() + "|" +
					this.getTempo() + "|" + this.getMode() + "|" + 
					this.getSongCurrency() + "|" + this.getSongCurrencyRank() + "|" +
					this.getSongDiscovery() + "|" + this.getSongDiscoveryRank() + "|" +
					this.getSongHotttnesss() + "|" + this.getSongHotttnesssRank() + "|" +
					this.getSongType()+ "|" + this.getArtistHotttnesss() + "|" +
					this.getArtistHotttnesssRank() + "|" + this.getArtistDiscovery() + "|" +
					this.getArtistDiscoveryRank() + "|" + this.getArtistFamiliarity() + "|" +
					this.getArtistFamiliarityRank() + "|" + this.getArtistLocation();
		} catch (EchoNestException e) {
			e.printStackTrace();
		}
    	return songStr;
    }
}
