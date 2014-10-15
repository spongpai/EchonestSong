package com.echonest.api.v4;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;

public class SongParams extends Params {

    public static String SORT_TEMPO = "tempo";
    public static String SORT_DURATION = "duration";
    public static String SORT_LOUDNESS = "loudness";
    public static String SORT_ENERGY = "energy";
    public static String SORT_DANCEABILITY = "danceability";
    public static String SORT_ARTIST_FAMILIARITY = "artist_familiarity";
    public static String SORT_ARTIST_HOTTTNESSS = "artist_hotttnesss";
    public static String SORT_ARTIST_START_YEAR = "artist_start_year";
    public static String SORT_ARTIST_END_YEAR = "artist_end_year";
    public static String SORT_SONG_HOTTTNESSS = "song_hotttnesss";
    public static String SORT_LATITUDE = "latitude";
    public static String SORT_LONGITUDE = "longitude";
    public static String SORT_MODE = "mode";
    public static String SORT_KEY = "key";

    public void setTitle(String title) {
        add("title", title);
    }

    public void setArtist(String artist) {
        add("artist", artist);
    }

    public void addDescription(String desc) {
        add("description", desc);
    }

    public void setID(String id) {
        add("id", id);
    }

    public void setCombined(String combined) {
        add("combined", combined);
    }

    public void setArtistID(String artistID) {
        add("artist_id", artistID);
    }

    public void setResults(int results) {
        add("results", results);
    }

    public void setMaxTempo(float tempo) {
        add("max_tempo", tempo);
    }

    public void setMinTempo(float tempo) {
        add("min_tempo", tempo);
    }

    public void setMaxDuration(float val) {
        add("max_duration", val);
    }

    public void setMinDuration(float val) {
        add("min_duration", val);
    }

    public void setMaxLoudness(float val) {
        add("max_loudness", val);
    }

    public void setMinLoudness(float val) {
        add("min_loudness", val);
    }

    public void setMaxArtistFamiliarity(float val) {
        add("artist_max_familiarity", val);
    }

    public void setMaxDanceability(float val) {
        add("max_danceability", val);
    }

    public void setMinDanceability(float val) {
        add("min_danceability", val);
    }

    public void setMaxEnergy(float val) {
        add("max_energy", val);
    }

    public void setMinEnergy(float val) {
        add("min_energy", val);
    }

    public void setMinArtistFamiliarity(float val) {
        add("artist_min_familiarity", val);
    }

    public void setMaxArtistHotttnesss(float val) {
        add("artist_max_hotttnesss", val);
    }

    public void setMinSongHotttnesss(float val) {
        add("song_min_hotttnesss", val);
    }

    public void setMaxSongHotttnesss(float val) {
        add("song_max_hotttnesss", val);
    }

    public void setMinArtistHotttnesss(float val) {
        add("artist_min_hotttnesss", val);
    }

    public void setMaxLongitude(float val) {
        add("max_longitude", val);
    }

    public void setMinLongitude(float val) {
        add("min_longitude", val);
    }

    public void setMaxLatitude(float val) {
        add("max_latitude", val);
    }

    public void setMinLatitude(float val) {
        add("min_latitude", val);
    }

    public void setMode(int mode) {
        add("mode", mode);
    }

    public void setKey(int key) {
        add("key", key);
    }

    public void setLimit(boolean limit) {
        add("limit", limit);
    }

    public void setLimitAny() {
        add("limit", "any");
    }

    public void setLimitAll() {
        add("limit", "all");
    }
    
    public void includeAll(){
    	includeAudioSummary();
        
        includeSongCurrency();
        includeSongCurrencyRank();
        includeSongDiscovery();
        includeSongDiscoveryRank();
        includeSongHotttnesss();
        includeSongHotttnesssRank();
        includeSongType();
        
        includeArtistDiscovery();
        includeArtistDiscoveryRank();
        includeArtistFamiliarity();
        includeArtistFamiliarityRank();
        includeArtistHotttnesss();
        includeArtistHotttnesssRank();
        includeArtistLocation();
    }

    public void includeAudioSummary() {
        add("bucket", "audio_summary");
    }
    
    
    public void includeArtistDiscovery() {
        add("bucket", "artist_discovery");
    }
    
    public void includeArtistDiscoveryRank() {
        add("bucket", "artist_discovery_rank");
    }

    public void includeArtistFamiliarity() {
        add("bucket", "artist_familiarity");
    }
    
    public void includeArtistFamiliarityRank() {
        add("bucket", "artist_familiarity_rank");
    }

    public void includeArtistHotttnesss() {
        add("bucket", "artist_hotttnesss");
    }

    public void includeArtistHotttnesssRank() {
        add("bucket", "artist_hotttnesss_rank");
    }
    public void includeArtistLocation() {
        add("bucket", "artist_location");
    }
    
    public void includeSongCurrency() {
        add("bucket", "song_currency");
    }
    
    public void includeSongCurrencyRank() {
        add("bucket", "song_currency_rank");
    }
    
    public void includeSongDiscovery() {
        add("bucket", "song_discovery");
    }
    
    public void includeSongDiscoveryRank() {
        add("bucket", "song_discovery_rank");
    }
    
    public void includeSongHotttnesss() {
        add("bucket", "song_hotttnesss");
    }

    public void includeSongHotttnesssRank() {
        add("bucket", "song_hotttnesss_rank");
    }
    
    public void includeSongType() {
        add("bucket", "song_type");
    }

    public void includeTracks() {
        add("bucket", "tracks");
    }

    public void includeID7DigitalUS(){
    	add("bucket", "id:7digital-US");
    }
    
   
    

    public void addIDSpace(String idspace) {
        if (!idspace.startsWith("id:")) {
            idspace = "id:" + idspace;
        }
        add("bucket", idspace);
    }

    /**
     * Constrains results to artists that have an earliest start year before the
     * given year
     *
     * @param year
     */
    public void setArtistStartYearBefore(int year) {
        set("artist_start_year_before", year);
    }

    /**
     * Constrains results to artists that have an earliest start year after the
     * given year
     *
     * @param year
     */
    public void setArtistStartYearAfter(int year) {
        set("artist_start_year_after", year);
    }

    /**
     * Constrains results to artists that have a latest end year before the
     * given year
     *
     * @param year
     */
    public void setArtistEndYearBefore(int year) {
        set("artist_end_year_before", year);
    }

    /**
     * Constrains results to artists that have a latest end year after the given
     * year
     *
     * @param year
     */
    public void setArtistEndYearAfter(int year) {
        set("artist_end_year_after", year);
    }

    /**
     * Constrains results to artists that were active at any time during the
     * given range
     *
     * @param startYear the start year of interest
     * @param endYear the end year of interest
     */
    public void setArtistActiveDuring(int startYear, int endYear) {
        set("artist_active_during", startYear + ":" + endYear);
    }

    /**
     * Constrains results to artists that were active throughout all of the the
     * given range
     *
     * @param startYear the start year of interest
     * @param endYear the end year of interest
     */
    public void setArtistActiveThroughout(int startYear, int endYear) {
        set("artist_active_throughout", startYear + ":" + endYear);
    }

    public void sortBy(String sort, boolean ascending) {
        if (ascending) {
            sort = sort + "-asc";
        } else {
            sort = sort + "-desc";
        }
        add("sort", sort);
    }

    /**
     * Include songs of the given type
     *
     * @param type the song type
     * @param flag the state
     */
    public void addSongType(Song.SongType type, Song.SongTypeFlag flag) {
        add("song_type", type.toString() + ":" + flag.toString().toLowerCase());
    }

    /**
     * Steer towards artists that match the given description
     *
     * @param term the term to boost
     * @param boost the boost
     */
    public void addDescription(EchoNestAPI.DescriptionType type, String term) {
        add(type.toString(), term);
    }

    /**
     * Steer towards artists that match the given description, with a boost
     *
     * @param term the term to boost
     * @param boost the boost
     */
    public void addDescription(EchoNestAPI.DescriptionType type, String term, float boost) {
        String tboost = term + "^" + boost;
        add(type.toString(), tboost);
    }

    /**
     * Bans the given term from appearing on the session
     *
     * @param term the term to ban
     */
    public void banDescription(EchoNestAPI.DescriptionType type, String term) {
        String bannedTerm = "-" + term;
        add(type.toString(), bannedTerm);
    }

    /**
     * Bans the given term from appearing on the session
     *
     * @param term the term to ban
     */
    public void requireDescription(EchoNestAPI.DescriptionType type, String term) {
        String requiredTerm = "+" + term;
        add(type.toString(), requiredTerm);
    }
    
    
    
    public void songShowAll (Song song) throws EchoNestException {
    	// song summary
    	System.out.printf("%s\n", song.getTitle());
        System.out.printf("   song ID: %s\n", song.getID());
        System.out.printf("   artist: %s\n", song.getArtistName());
        System.out.printf("   dur   : %.3f\n", song.getDuration());
        System.out.printf("   BPM   : %.3f\n", song.getTempo());
        System.out.printf("   Mode  : %d\n", song.getMode());
        
        // song data
        System.out.printf("   S cur : %.3f\n", song.getSongCurrency());
        System.out.printf("   S curRank : %.3f\n", song.getSongCurrencyRank());
        System.out.printf("   S dis : %.3f\n", song.getSongDiscovery());
        System.out.printf("   S disRank : %.3f\n", song.getSongDiscoveryRank());
        System.out.printf("   S hot : %.3f\n", song.getSongHotttnesss());
        System.out.printf("   S hotRank : %.3f\n", song.getSongHotttnesssRank());
        System.out.printf("   S type : %s\n", song.getSongType());

        // artist data
        System.out.printf("   A hot : %.3f\n", song.getArtistHotttnesss());
        System.out.printf("   A hotRank : %.3f\n", song.getArtistHotttnesssRank());
        System.out.printf("   A dis : %.3f\n", song.getArtistDiscovery());
        System.out.printf("   A disRank : %.3f\n", song.getArtistDiscoveryRank());
        System.out.printf("   A fam : %.3f\n", song.getArtistFamiliarity());
        System.out.printf("   A famRank : %.3f\n", song.getArtistFamiliarityRank());
        System.out.printf("   A loc : %s\n", song.getArtistLocation());
         
    }
}
