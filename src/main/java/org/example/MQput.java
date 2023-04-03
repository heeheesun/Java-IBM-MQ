package org.example;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;


import com.ibm.mq.MQAsyncStatus;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

public class MQput {


    public static List<String> readPerLine(File file) {
        // 파일을 줄별로 읽어오는 함수
        // BufferedReader : 버퍼를 이용해서 읽음(입력) 엔터만 경계로 인식, 받은 데이터가 String으로 고정
        // BufferedWriter : 버퍼를 이용해서 씀(출력)
        // readLine : 데이터를 한 줄 씩 읽어옴
        List<String> list = new ArrayList<String>();
        BufferedReader reader = null;
        String line = "";

        try {
            reader = new BufferedReader(new FileReader(file)); // 파일 읽음
            while ((line = reader.readLine()) != null) { // 한 줄 씩 읽어온 데이터가 null이 아닐 경우
                list.add(line); // 읽어온 데이터를 list에 추가
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close(); // BufferedReader close()
                    reader = null; // BufferedReader 초기화
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static void systemEncoding() {
        // 시스템 문자 인코딩
        // IO Exception Occurred: Input length = 1 에러를 해결하기 위함 (현재는 지워도 에러가 나지 않는다)
        // com.ibm.mq.cfg.jmqi.UnmappableCharacterAction: 인코딩 및 디코딩 시 번역할 수 없는 데이터에 대해 수행할 조치를 지정
        // com.ibm.mq.cfg.jmqi.UnmappableCharacterReplacement: 인코딩 작업에서 문자를 매핑할 수 없을 때 적용할 대체 바이트를 설정하거나 가져옴
        System.setProperty("com.ibm.mq.cfg.jmqi.UnmappableCharacterAction", "REPLACE");
        System.setProperty("com.ibm.mq.cfg.jmqi.UnmappableCharacterReplacement", "63");
    }

    public static MQMessage messageEncoding(MQMessage message) {
        // 메세지 인코딩
        // MQFMT_STRING: 애플리케이션 메시지 데이터는 SBCS 문자열(1바이트 문자 세트) 또는 DBCS 문자열(2바이트 문자 세트)일 수 있습니다.
        //               MQGET 호출에 MQGMO_CONVERT 옵션이 지정된 경우 이 형식의 메시지를 변환할 수 있습니다.
        message.format = MQConstants.MQFMT_STRING;
//            message.encoding = 546; // window
        // MQENC_NATIVE: 숫자 코드를 사용해서 시스템 인코딩
        message.encoding = MQConstants.MQENC_NATIVE;
        message.characterSet = 1208; // UTF-8

        return message;
    }

    public static void putMQmessage() {
        // 큐 매니저에 연결하기 위한 커넥션 생성
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        //로컬 큐에 접속할 때는 사용하지 않는 요소들
//        props.put(MQConstants.CHANNEL_PROPERTY, "SYSTEM.ADMIN.SVRCONN"); //OR TEST_SVRCONN_CH
//        props.put(MQConstants.PORT_PROPERTY, 1414);
//        props.put(MQConstants.HOST_NAME_PROPERTY, "localhost");


        Properties prop = readProperties.readProp();
        String qManager = prop.getProperty("qManager");
        String queueName = prop.getProperty("qName");
        MQQueueManager qMgr = null;

        String fileRoute =  prop.getProperty("fileRoute");
        String fileName =  prop.getProperty("putFileName");
        File file = new File(fileRoute + fileName + ".txt"); // 읽어올 파일 경로와 이름 설정

        systemEncoding();


        try {
            // 해시 테이블을 사용하여 이름 지정된 큐 관리자에 대한 연결을 작성
            qMgr = new MQQueueManager(qManager, props);
            // Queue open options
            // MQOO_OUTPUT: 메세지를 put 하기 위해 큐를 연다
            // MQOO_INPUT_AS_Q_DEF: 대기열 정의 기본값을 사용하여 메시지를 가져오려면 대기열을 연다
            int openOptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_INPUT_AS_Q_DEF;

            // 기본 큐 관리자 이름 및 대체 사용자 ID 값을 사용하여 이 큐 관리자에서 WebSphere MQ 큐에 대한 액세스를 설정
            MQQueue queue = qMgr.accessQueue(queueName, openOptions);

            // 옵션이 설정되지 않은 상태로 객체를 구성하고 공백 resolveQueueName 및 resolveQueueManagerName을 생성
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            // MQPMO_ASYNC_RESPONSE: 애플리케이션이 큐 관리자가 호출을 완료할 때까지 기다리지 않고 MQPUT 또는 MQPUT1 조작이 완료되도록 요청
            pmo.options = MQConstants.MQPMO_ASYNC_RESPONSE;

            // 메세지 생성
            MQMessage message = new MQMessage();
            messageEncoding(message);

            // 메세지 읽어오기
            List<String> list = readPerLine(file);
            String str = list.toString();
            System.out.println("str = " + str);
            // readPerLine(BufferReader-leadLine)으로 메세지를 읽어온 경우 일정 형식을 갖게 됨. 변환해준다.
            int length = str.length();
            str = str.substring(1, length - 1);


            // 메세지 입력
            message.writeString(str);

            // 큐에 메세지와 연결 정보를 담아서 put
            queue.put(message, pmo);
            queue.close();

            // Junit에서 수행
            // MQAsyncStatus를 사용하여 이전 MQI 활동의 상태를 조회
            MQAsyncStatus asyncStatus = qMgr.getAsyncStatus(); // 큐 관리자에서 비동기 오류 상태를 요청
            assertEquals(1, asyncStatus.putSuccessCount); // 두 객체의 값이 같은지 여부를 체크

        } catch (MQException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                qMgr.disconnect();
            } catch (MQException e) {
                e.printStackTrace();
            }
        }
    }
}