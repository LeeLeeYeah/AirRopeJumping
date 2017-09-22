import java.io.IOException;



import libsvm.*;

public class LibSVMTest {

	/**JAVA test code for LibSVM
	 * @author yangliu
	 * @throws IOException 
	 * @blog http://blog.csdn.net/yangliuy
	 * @mail yang.liu@pku.edu.cn
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Test for svm_train and svm_predict
		//svm_train: 
		//	  param: String[], parse result of command line parameter of svm-train
		//    return: String, the directory of modelFile
		//svm_predect:
		//	  param: String[], parse result of command line parameter of svm-predict, including the modelfile
		//    return: Double, the accuracy of SVM classification
		String filepath="C:/Users/chenlingf/Desktop/NLPLibSVM/";
////		for(int i=1;i<=10;i++)
////		{
//		String[] trainArgs = {"-t","0","5train"};//directory of training file
//		String modelFile = svm_train.main(trainArgs);
//		String[] testArgs = {"testfile", modelFile, filepath+"result"};//directory of test file, model file, result file
//		Double accuracy = svm_predict.main(testArgs);
//		System.out.println("SVM Classification is done! The accuracy is " + accuracy);
////		}
		
		
//
		String[] crossValidationTrainArgs = {"-v", "10","5train"};// 10 fold cross validation
		String modelFile = svm_train.main(crossValidationTrainArgs);
		System.out.print("Cross validation is done! The modelFile is " + modelFile);
	}

}
