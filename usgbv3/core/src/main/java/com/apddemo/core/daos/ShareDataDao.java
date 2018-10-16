package com.apddemo.core.daos;

import com.apddemo.core.models.ShareData;

public interface ShareDataDao {

	public boolean insertShareData(ShareData data);
	public ShareData getShareData(String key);
	
}
