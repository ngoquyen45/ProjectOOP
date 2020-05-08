View keystore details: 
	keytool -list -v -keystore DMSPlusDebug.jks
	keytool -list -v -keystore DMSPlusRelease.jks
================================================================
Password:
	Debug: 	 VmpCWmVUbE1XbGhzVkdSSE9YbGE=
	Release: LZXlTdG9yZS9ETVNQbHVzUmVsZWFzZS5qa3M=
==================
SHA1:
	Debug:	 FC:85:5D:4F:5A:08:00:B1:A3:BA:7A:E4:88:BF:37:2D:D3:1D:E3:ED
	Release: 92:F5:6A:B0:CC:6E:5C:81:8E:06:90:68:79:31:76:53:F3:54:D0:2E
==================
keytool -genkey -v -keystore DMSPlusDebug.jks -alias dmsplus -keyalg RSA -keysize 2048 -validity 10000
==================
CN=Thanh Nguyen, OU=Viettel ICT, O=Viettel Group, L=Nam Tu Liem, ST=Ha Noi, C=VN