package com.example.widgetservice;

import com.example.widgetservice.IGeneratedValueListener;
import com.example.widgetservice.GeneratedValueResult;

interface IBoundService {
	void putSeed(int seed);
	int getValue();
	void setServiceRunning(boolean value);

	GeneratedValueResult getCurrentResult();
	void addListener(in IGeneratedValueListener listener);
	void removeListener(in IGeneratedValueListener listener);
}