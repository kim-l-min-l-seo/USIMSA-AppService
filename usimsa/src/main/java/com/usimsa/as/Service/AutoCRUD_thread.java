package com.usimsa.as.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.usimsa.as.Mapper.DataMapper;
import com.usimsa.as.VO.DataVO;

public class AutoCRUD_thread extends Thread {
	
	String rw;
	private final SqlSession sqlSession;
	
	public AutoCRUD_thread(String rw, SqlSession sqlSession) {
		super();
		this.rw = rw;
		this.sqlSession = sqlSession;
		
	}
	
	private Logger logger = LoggerFactory.getLogger(AutoCRUD_thread.class);
	
	static int count = 0;
	static String msg = null;
	
	@Override
	public void run() {
		count ++;
		DataVO dataVO = new DataVO();
		dataVO.setDate(new Timestamp(System.currentTimeMillis()));
		dataVO.setContents(Integer.toString(count));
		dataVO.setRw(rw);
		try {
			dataVO.setWebServer_name(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DataMapper dm = sqlSession.getMapper(DataMapper.class);
		try {
			dm.insertData(dataVO);
		} catch (Exception e) {
			if (msg != null) {
				msg = null;
			}
			msg = "Exception : "+e;
		} finally {
			if (msg != null) {
				dataVO.setContents(msg);
				dm.insertData(dataVO);
				msg = null;
			}else {
				if (!dataVO.getContents().equals(Integer.toString(count))){
					dataVO.setContents(Integer.toString(count));
					dm.insertData(dataVO);
				}
			}
		}
//		logger.info(dataVO.getDate()+">>>"+count);
	}
	
}
