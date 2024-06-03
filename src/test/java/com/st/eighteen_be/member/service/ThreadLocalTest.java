package com.st.eighteen_be.member.service;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalTest {

    public static void main(String[] args){

        // 전화 번호 입력 후 인증 번호 전송 눌렀을 때
        new ThreadLocalTest().runTest();
    }

    static class ThreadTest extends Thread {
        private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
        private final String code;
        private final String name;

        public ThreadTest(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public void run(){
            System.out.printf("%s Started, Random Code = %s, ThreadLocal: %s\n",name, code, threadLocal.get());
            threadLocal.set(code);
            System.out.printf("%s Finished, Random Code = %s, ThreadLocal: %s\n", name, code, threadLocal.get());
            threadLocal.remove(); // WHEN(검증 완료 OR 뒤로 가기 OR 시간 초과 OR 검증 실패)
        }
    }
    // 스레드 풀 선언
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public void runTest(){

        for(int threadCount = 1; threadCount <= 5; threadCount++){
            final String name = "thread-" + threadCount;

            // 4자리 랜덤 인증 코드 생성
            SecureRandom rand = new SecureRandom();
            StringBuilder randomNum = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                String random = Integer.toString(rand.nextInt(10));
                randomNum.append(random);
            }
            final String code = randomNum.toString();

            final ThreadTest thread = new ThreadTest(code, name);
            executorService.execute(thread);
        }

        executorService.shutdown(); // 스레드 풀 종료

        while(true){
            try{
                if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                System.err.println("ERROR: " + e);
                executorService.shutdownNow();
            }
        }

        //then
        System.out.println("All threads are finished!");

    }

}
