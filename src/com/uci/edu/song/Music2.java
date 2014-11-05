package com.uci.edu.song;

import com.echonest.api.v4.Song;

public class Music2 {
	public int fileid;
	public String date;
	public String postTitle, artist, songTitle;
	public Song song;
	public int[] termsArr, genresArr;
	public String ssnumber;
	
	public Music2(){
		
	}
	
	public Music2(int fileid, String date, String postTitle, String artist, String songTitle){
		this.fileid = fileid;
		this.date = date;
		this.postTitle = postTitle;
		this.artist = artist;
		this.songTitle = songTitle;
		this.song = null;
	}
	public void setSSN(String ssn){
		this.ssnumber = ssn;
	}
	public void setSong(Song s){
		this.song = s;
	}

	public Song getSong(){
		return this.song;
	}
	
	public String getMusicHeader(){
		return "fileid||datesaved||posttitle||artist||songtitle||" + 
				this.song.getParamsSetHeaderShort();
	}
	
	public String inputToString(){
		String output = this.fileid + "|" + this.date + "|" + this.postTitle + "|" + this.artist + "|" + this.songTitle;
		return output;
	}
	
	public String toString(){
		String output = this.fileid + "|" + this.date + "|" + this.postTitle + "|" + this.artist + "|" + this.songTitle;
		if(song != null){
			output += song.toStringFull();
		}
		return output;
	}

}
