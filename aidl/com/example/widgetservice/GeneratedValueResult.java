package com.example.widgetservice;

import android.os.Parcel;
import android.os.Parcelable;

public class GeneratedValueResult implements Parcelable {
	
	private int value	= 0;
	private int seed	= 0;
	private int nGener	= 0;
	
	public GeneratedValueResult(){
		
	}
	
	private GeneratedValueResult(Parcel in){
		this.setSeed(in.readInt());
		this.setValue(in.readInt());
		this.setnGener(in.readInt());
	}

	public static final Parcelable.Creator<GeneratedValueResult> CREATOR
					= new Parcelable.Creator<GeneratedValueResult>() {

						@Override
						public GeneratedValueResult createFromParcel(Parcel in) {
							if(in != null)
								return new GeneratedValueResult(in);
							return new GeneratedValueResult();
						}

						@Override
						public GeneratedValueResult[] newArray(int size) {
							return new GeneratedValueResult[size];
						}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the nGener
	 */
	public int getnGener() {
		return nGener;
	}

	/**
	 * @param nGener the nGener to set
	 */
	public void setnGener(int nGener) {
		this.nGener = nGener;
	}
}
