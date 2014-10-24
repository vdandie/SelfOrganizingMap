
import com.wiseaux.intersector.Intersector;
import com.wiseaux.setCreator.SetCreatorEnhanced;

/**
 * This is where the SelfOrganizingMap is going to be run.
 * 
 * @author Daniel Swain
 */
public class TestSOM {
    
    public static void main(String[] args){
        SetCreatorEnhanced som = new SetCreatorEnhanced();
//        som.run();
//        som.train(10);
//        som.runForSpecialSet();
//        som.trainAllButOneRecord();
        Intersector thing = new Intersector();
//        thing.read(10);
//        thing.run();
        thing.readSpecialCase();
        thing.run_2();
//        thing.resultsToFile("result");
//        thing.totalResultsFile();
        thing.specialTotalResultsFile();
    }
        
}
