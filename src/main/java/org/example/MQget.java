package org.example;

import java.io.*; //import classes for IO operation
import com.ibm.mq.*; //import classes for MQSeries

public class MQget
{
    public static void getMQmessage() throws IOException
    {
        String qmgrname="TESTQM";
        String qname="Local_Q1";
        String fileRoute = "C:\\Users\\heehe\\OneDrive\\바탕 화면\\test\\";
        String fileName = "makeTestFile";
        try{
            //로컬 큐에 접속할 때는 사용하지 않는 요소들
//            MQEnvironment.hostname = "";
//            MQEnvironment.channel = "";
//            MQEnvironment.port = Integer.parseInt("");
            MQQueueManager qmgr = new MQQueueManager(qmgrname); // 이름 지정된 큐 관리자에 대한 연결을 작성

            // Queue open option
            // MQC.MQOO_INPUT_AS_Q_DEF: 대기열 정의 기본값을 사용하여 메시지를 가져오려면 엽니다.
            // MQC.MQOO_INQUIRE: 조회를 위해 열기 - 특성을 조회하려는 경우에 필요합니다.
            int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_INQUIRE;
            // 이 큐 관리자에서 MQ 큐에 대한 액세스를 설정하여 메시지를 가져오거나 찾아보고, 메시지를 넣고, 큐의 속성에 대해 조회하거나 큐의 속성을 설정
            MQQueue que = qmgr.accessQueue(qname,openOptions,null,null,null);
            int depth = que.getCurrentDepth(); // 큐에 들어간 메세지의 갯수
            int t_dep = depth;
            System.out.println("depth = " + depth);

            MQMessage msg;
            MQGetMessageOptions gmo;

            int filenum =1; //메세지 큐에서 모든 메세지를 한번에 꺼낼 때 출력 파일 넘버링을 위한 변수
            while(t_dep>0)
            {
                msg = new MQMessage();
                gmo = new MQGetMessageOptions();

                System.out.println("in while t_dep = " + t_dep);
                que.get(msg,gmo);
                String msg_str = msg.readLine();
                System.out.println("msg_str = " + msg_str);

                // putMQmessage로 큐에 메세지를 넣은 경우 일정 형식을 갖게 됨
                // 그 형식을 가지고 있는지 확인하고 그럴 경우 다시 변환
                int length = msg_str.length();
                String first = msg_str.substring(0, 1);
                String last = msg_str.substring(length-1);

                if (first.equals("[") && last.equals("]"))
                    msg_str = msg_str.substring(1, length - 1);

                String enter_msg_str = msg_str.replaceAll(", ", System.getProperty("line.separator"));

                // file에 들어갈 내용
                String[] change_msg_str = msg_str.split(", ");
                System.out.println("<change_msg_str>");
                for(int j=0; j<change_msg_str.length; j++)
                    System.out.println(change_msg_str[j]);

                // 생성 파일 경로와 이름, 확장자 지정
                File file = new File(fileRoute + fileName + filenum +".txt");
                FileOutputStream fos = null;
                filenum++;
                try {
                    // FileOutputStream : 데이터를 파일에 바이트 스트림으로 저장하기 위해 사용
                    // FileOutputStream(File file) : 주어진 File 객체가 가리키는 파일을 쓰기 위한 객체를 생성. 기존의 파일이 존재할 때는 그 내용을 지우고 새로운 파일을 생성.
                    fos = new FileOutputStream(file);

                    byte[] content = enter_msg_str.getBytes(); // byte형 content에 엔터 값을 변환한 메세지를 넣음

                    fos.write(content); // 생성한 파일에 메세지 작성
                    fos.flush(); // 출력하고 버퍼의 내용을 비움
                    fos.close(); // 파일 입력 스트림 닫음

                    System.out.println("DONE");
                    System.out.println("---------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                    } catch (Exception e) {

                    }
                }

                t_dep=t_dep-1;
            }

            qmgr.commit(); // 애플리케이션이 동기점에 도달했음을 큐 관리자에게 표시
        }catch(MQException mq){
            System.out.println(mq.getMessage());
        }

    }
}