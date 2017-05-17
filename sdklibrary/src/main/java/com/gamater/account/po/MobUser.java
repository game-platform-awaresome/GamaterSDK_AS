package com.gamater.account.po;

import java.io.Serializable;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class MobUser implements Serializable {
	private static final long serialVersionUID = -7876899340630800776L;
	private String type; // 用户类型，SDK内部数据
	private String userid; // 用户唯一ID
	private String token; // 用户登录token
	private String email = ""; // 用户email
	private String passwd = ""; // 弃用
	private String fbUserId = ""; // 用户的FacebookId，必须isFacebook为true
	private String fbNickName = ""; // 用户的Facebook昵称，必须isFacebook为true
	private boolean isFacebook = false; // 用户是否通过Facebook登录
	private int status = 0;

	private String roleId = "";
	private String roleName = "";
	private String serverId = "";
	private String serverName = "";
	private int level = 0;
	private String profession = "";
	private int power = 0;
	private int gold = 0;

	private int coinFunds;
	private int loginType;

	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	public int getCoinFunds() {
		return coinFunds;
	}

	public void setCoinFunds(int coinFunds) {
		this.coinFunds = coinFunds;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		if (roleId != null) {
			this.roleId = roleId;
		}
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public MobUser() {
	}

	public String toCacheJson() {
		JSONObject o = new JSONObject();
		try {
			o.put("userid", userid);
			o.put("type", type);
			o.put("token", token);
			o.put("email", email);
			o.put("roleId", roleId);
			o.put("roleName", roleName);
			o.put("serverId", serverId);
			o.put("serverName", serverName);
			o.put("level", level);
			o.put("profession", profession);
			o.put("power", power);
			o.put("gold", gold);
			o.put("loginType", loginType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}

	public MobUser toCacheUser(String jsonStr) {
		try {
			JSONObject o = new JSONObject(jsonStr);
			setUserid(o.optString("userid"));
			setType(o.optString("type"));
			setToken(o.optString("token"));
			setEmail(o.optString("email"));
			setRoleId(o.optString("roleId"));
			setRoleName(o.optString("roleName"));
			setServerId(o.optString("serverId"));
			setServerName(o.optString("serverName"));
			setLevel(o.optInt("level"));
			setProfession(o.optString("profession"));
			setPower(o.optInt("power"));
			setGold(o.optInt("gold"));
			setLoginType(o.optInt("loginType"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return this;
	}

	public MobUser(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			this.status = obj.getInt("status");
			if (this.status == 1) {
				JSONObject data = obj.getJSONObject("data");
				Iterator<String> iter = data.keys();
				boolean hasType = false;
				while (iter.hasNext()) {
					String key = iter.next();
					if (key.equalsIgnoreCase("type")) {
						this.type = data.getString("type");
						hasType = true;
					} else if (key.equalsIgnoreCase("email")) {
						this.email = data.getString("email");
					} else if (key.equalsIgnoreCase("passwd")) {
						this.passwd = data.getString("passwd");
					} else if (key.equalsIgnoreCase("isFacebook")) {
						this.isFacebook = data.getBoolean("isFacebook");
					} else if (key.equalsIgnoreCase("fbUserId")) {
						this.fbUserId = data.getString("fbUserId");
					} else if (key.equalsIgnoreCase("fbNickName")) {
						this.fbNickName = data.getString("fbNickName");
					}
				}
				if (!hasType) {
					this.type = "1";
				}
				this.userid = data.getString("userid");
				this.token = data.getString("token");
				this.loginType = data.getInt("loginType");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getFbUserId() {
		return fbUserId;
	}

	public void setFbUserId(String fbUserId) {
		this.fbUserId = fbUserId;
	}

	public String getFbNickName() {
		return fbNickName;
	}

	public void setFbNickName(String fbNickName) {
		this.fbNickName = fbNickName;
	}

	public boolean isFacebook() {
		return isFacebook;
	}

	public void setFacebook(boolean isFacebook) {
		this.isFacebook = isFacebook;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getGold() {
		return gold;
	}

	public int getPower() {
		return power;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof MobUser))
			return false;
		MobUser u = (MobUser) o;
		if (u.getUserid().equals(getUserid()))
			return true;
		return false;
	}
}
