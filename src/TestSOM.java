
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
        som.runForPreCreatedSets();
        som.trainPreCreatedSets();
        Intersector thing = new Intersector();
        thing.read(10);
        thing.run();
        thing.resultsToFile("result");
        thing.totalResultsFile();
    }
        
}
