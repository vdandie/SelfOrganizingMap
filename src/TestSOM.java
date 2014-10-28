
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
        Intersector thing = new Intersector();
        som.run();
        som.train(10);
        thing.read(10);
        thing.run();
        thing.resultsToFile("result");
        thing.totalResultsFile();
//        som.runForSpecialSet();
//        som.trainAllButOneRecord();
//        thing.readSpecialCase();
//        thing.run_2();
//        thing.specialTotalResultsFile();
        
    }
        
}
