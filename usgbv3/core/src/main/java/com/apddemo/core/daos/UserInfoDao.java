package com.apddemo.core.daos;

import com.apddemo.core.entity.UserInfo;

public interface UserInfoDao {

	public boolean insertData(UserInfo data);
	public UserInfo getUser(String sso_id);
	public String getUserId(UserInfo userInfo);
}
