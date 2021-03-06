import java.util.*;
import java.util.concurrent.*;
import static java.util.Arrays.asList;
import java.net.*;
import org.jblas.DoubleMatrix;

/*
UnitHub starts RegisterServer Thread

and when at least one volunteer has signed in
UnitHub can call a distributed mathon (eg. distributedAdd)
which will break up the problem into peaces where each peace is run as Unit in
seperate Callable Thread

private List<InetSocketAddress> register;
is what methods defined in IRegister is accessing
it contains (ip,port) of all the volunteers who has signed interface MyInterface extends Parent {

}
*/
public class UnitHub implements IRegister {

    private List<InetSocketAddress> register;
    UnitHub(){
      register = new ArrayList<InetSocketAddress>();

    }
    public int getRegisterSize(){
      return register.size();
    }
    public List<InetSocketAddress> getRegister(){
      return register;
    }
    public void addToRegister(InetSocketAddress addr){
      if(!getRegister().contains(addr))
        getRegister().add(addr);
    }
    public void removeFromRegister(InetSocketAddress addr){
      getRegister().remove(addr);
    }
    public void printRegister(){
      System.out.println(getRegisterSize()+" registered units:\n"+getRegister());
    }

    public DoubleMatrix distributedAdd(DoubleMatrix A, DoubleMatrix B) throws Exception {

      /*
        use getRegisterSize() to find number of volunteers which is the
            maximum number of parts we can break the problem into

        use getRegister() to get a List of volunteer addresses

        todo - break the problem into maximum number possible given operand size
              but less than getRegisterSize()
              note: add each sub problem into l and executor.invokeAll(l); will return
                    list of Future containing answer to each sub problems
      */
      DoubleMatrix ans;
      ExecutorService executor = Executors.newCachedThreadPool();
      List<Unit> l = new ArrayList<>();
      l.add(new Unit(A, B, '+', getRegister().get(0) ));
      List <Future<DoubleMatrix> > results = executor.invokeAll(l);
      executor.shutdown();

      /*
      for (Future<DoubleMatrix> result : results) {
         System.out.println(result.get());
      }
      */
      ans = results.get(0).get();
      return ans;
    
    }

    public static void main(String[] args) throws Exception {
        UnitHub hub = new UnitHub();
        RegisterServer rs = new RegisterServer(hub, new InetSocketAddress( args[0], Integer.parseInt(args[1]) ) );

        DoubleMatrix A = DoubleMatrix.ones(3,3);
        DoubleMatrix B = DoubleMatrix.ones(3,3);

        Scanner sc = new Scanner(System.in);
        System.out.println(">>Enter when more than one volunteer signed in");
        sc.nextLine();
        if(hub.getRegisterSize() > 0){
          hub.printRegister();
          DoubleMatrix C = hub.distributedAdd(A, B);
          System.out.println("A + B = " + C);
        }else{
          System.out.println("no volunteer");
        }






    }
}

/*
method 1
ExecutorService executor = Executors.newCachedThreadPool();
List<Unit> l = Arrays.asList(new Unit(...), new Unit(...), new Unit(...));
List <Future<Long>> results = executor.invokeAll(l);
executor.shutdown();
for (Future<Long> result : results) {
   System.out.println(result.get());
}


method 2
ExecutorService executor = Executors.newCachedThreadPool();
Future a = executor.submit( new Unit(...));
Future b = executor.submit( new Unit(...));
executor.shutdown();
System.out.println(a.get());
System.out.println(b.get());

*/
