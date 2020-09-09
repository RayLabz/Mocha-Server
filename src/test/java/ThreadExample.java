//public class ThreadExample {
//
//    public static class MyRunnable implements Runnable {
//        private boolean running = true;
//
//        public boolean isRunning() {
//            return running;
//        }
//
//        public void setRunning(boolean running) {
//            this.running = running;
//        }
//
//        @Override
//        public void run() {
//            int i = 0;
//            while (running) {
//                System.out.println(i);
//                try {
//                    Thread.sleep(1000);
//                    i++;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("Stopped");
//        }
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//
//        MyRunnable myRunnable = new MyRunnable();
//        Thread t = new Thread(myRunnable);
//
//        t.start();
//
//        Thread.sleep(5000);
//
//        myRunnable.setRunning(false);
//
//
//
//
//    }
//
//}
