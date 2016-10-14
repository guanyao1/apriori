package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
public class AprioriAlgorithm {
	private int minSup;
	 private static List<String> data;
	 private static List<Set<String>> dataSet;
	 
	 public static void main(String[] args) {
	   
	  long startTime = System.currentTimeMillis();
	  AprioriAlgorithm apriori = new AprioriAlgorithm();
	  
	  //ʹ�����еĲ��Լ�
	  data = apriori.buildData();
	  
	  //������С֧�ֶ�
	  apriori.setMinSup(2);
	  //�������ݼ�
	  data = apriori.buildData();
	  
	  //����Ƶ��1�
	  List<Set<String>> f1Set = apriori.findF1Item(data);
	  apriori.printSet(f1Set, 1);
	  List<Set<String>> result = f1Set;
	  
	  int i = 2;
	  do{
	   result = apriori.arioriGen(result);
	   apriori.printSet(result, i);
	   i++;
	  }while(result.size() != 0);
	  long endTime = System.currentTimeMillis();
	  System.out.println("����ʱ�� " + (endTime - startTime) + "ms");
	 }
	 public void setMinSup(int minSup){
	  this.minSup = minSup;
	 }
	 
	 /**
	  * ����ԭʼ���ݼ�������Ϊ֮�ṩ������Ҳ���Բ��ṩ
	  * ������ṩ��������������Ĭ�Ϲ�������ݼ�
	  * ����ṩ����Ϊ�ļ�������ʹ���ļ��е����ݼ�
	  */
	 List<String> buildData(String...fileName){
	  List<String> data = new ArrayList<String>();
	  if(fileName.length != 0){
	   File file = new File(fileName[0]);
	   try{
	    BufferedReader reader = new BufferedReader(new FileReader(file));
	    String line;
	    while( ( line = reader.readLine()) != null ){
	     data.add(line);
	    }
	   }catch (FileNotFoundException e){
	    e.printStackTrace();
	   }catch (IOException e){
	    e.printStackTrace();
	   }
	  }else{
	   data.add("I1 I2 I5");
	   data.add("I2 I4");
	   data.add("I2 I3");
	   data.add("I1 I2 T4");
	   data.add("I1 I3");
	   data.add("I2 I3");
	   data.add("I1 I3");
	   data.add("I1 I2 I3 I5");
	   data.add("I1 I2 I3");
	  }
	  
	  dataSet = new ArrayList<Set<String>>();
	  Set<String> dSet;
	  for(String d : data){
	   dSet = new TreeSet<String>();
	   String[] dArr = d.split(" ");
	   for(String str : dArr){
	    dSet.add(str);
	   }
	   dataSet.add(dSet);
	  }
	  return data;
	 }
	 
	 /**
	  * �ҳ���ѡ1�
	  * @param data
	  * @return result
	  */
	 List<Set<String>> findF1Item(List<String> data){
	  List<Set<String>> result = new ArrayList<Set<String>>();
	  Map<String, Integer> dc = new HashMap<String, Integer>();
	  for(String d : data){
	   String[] items = d.split(" ");
	   for(String item : items){
	    if(dc.containsKey(item)) {
	     dc.put(item, dc.get(item)+1);
	    }else{
	     dc.put(item, 1);
	    }
	   }
	  }
	  Set<String> itemKeys = dc.keySet();
	  Set<String> tempKeys = new TreeSet<String>();
	  for(String str : itemKeys){
	   tempKeys.add(str);
	  }
	  
	  for(String item : tempKeys){
	   if(dc.get(item) >= minSup) {
	    Set<String> f1Set = new TreeSet<String>();
	    f1Set.add(item);
	    result.add(f1Set);
	   }
	  } 
	  return result;
	 }
	 
	 /**
	  * ����arioriGen������k-1�����k�
	  *@param preSet
	  *@return
	  * 
	  */
	 List<Set<String>> arioriGen(List<Set<String>> preSet) {
	  
	  List<Set<String>> result = new ArrayList<Set<String>>();
	  int preSetSize = preSet.size();
	  
	  for(int i = 0; i < preSetSize - 1; i++){
	   for(int j = i + 1; j < preSetSize; j++ ){
	    String[] strA1 = preSet.get(i).toArray(new String[0]);
	    String[] strA2 = preSet.get(j).toArray(new String[0]);
	    if(isCanLink(strA1, strA2)) {//�ж�����k-1��Ƿ�������ӳ�K�������
	     Set<String> set = new TreeSet<String>();
	     for(String str : strA1){
	      set.add(str);//��strA1����set������ǰK-1�
	     }
	     set.add((String) strA2[strA2.length-1]);//���ӳ�K�
	     //�ж�K��Ƿ���Ҫ���е����������Ҫ��cut��������뵽k����б���
	     if(!isNeedCut(preSet, set)) {
	      result.add(set);
	     }     
	    }
	   }
	  }
	  return checkSupport(result);//���صĶ���Ƶ��K�
	 }
	 
	 /**
	  * ��set�е�����������Ƚϲ����м��㣬���֧�ֶȴ���Ҫ����
	  * @param set
	  * @return
	  */
	 List<Set<String>> checkSupport(List<Set<String> > setList){
	  
	  List<Set<String>> result = new ArrayList<Set<String>>();
	  boolean flag = true;
	  int [] counter = new int[setList.size()];
	  for(int i = 0; i < setList.size(); i++){
	   
	   for(Set<String> dSets : dataSet) {
	    if(setList.get(i).size() > dSets.size()){
	     flag = true;
	    }else{
	     for(String str : setList.get(i)){
	      if(!dSets.contains(str)){
	       flag = false;
	       break;
	      }
	     }
	     if(flag) {
	      counter[i] += 1;
	     } else{
	      flag = true;
	     }
	    }
	   }
	  }
	  
	  for(int i = 0; i < setList.size(); i++){
	   if (counter[i] >= minSup) {
	    result.add(setList.get(i));
	   }
	  }
	  return result;
	 }
	 
	 /**
	  * �ж�������ܷ�ִ�����Ӳ���
	  * @param s1
	  * @param s2
	  * @return
	  */
	 boolean isCanLink(String [] s1, String[] s2){
	  boolean flag = true;
	  if(s1.length == s2.length) {
	   for(int i = 0; i < s1.length - 1; i ++){
	    if(!s1[i].equals(s2[i])){
	     flag = false;
	     break;
	    }
	   }
	   if(s1[s1.length - 1].equals(s2[s2.length - 1])){
	    flag = false;
	   }
	  }else{
	   flag = true;
	  }
	  return flag;
	 }
	 
	 /**
	  * �ж�set�Ƿ���Ҫ��cut
	  * 
	  * @param setList
	  * @param set
	  * @return
	  */
	 boolean isNeedCut(List<Set<String>> setList, Set<String> set) {//setListָƵ��K-1���setָ��ѡK�
	  boolean flag = false;
	  List<Set<String>> subSets = getSubset(set);//���K�������k-1�
	  for ( Set<String> subSet : subSets) {
	   //�жϵ�ǰ��k-1�set�Ƿ���Ƶ��k-1��г��֣�������֣�����Ҫcut
	     //��û�г��֣�����Ҫ��cut
	   if( !isContained(setList, subSet)){
	    flag = true;
	    break;
	   }
	  }
	  return flag;
	 }
	 /**
	  * ����:�ж�k���ĳk-1��Ƿ������Ƶ��k-1��б���
	  * 
	  * @param setList
	  * @param set
	  * @return
	  */
	 boolean isContained(List<Set<String>> setList, Set<String> set){
	  boolean flag = false;
	  int position = 0;
	  for( Set<String> s : setList  ) {
	   String [] sArr = s.toArray(new String[0]);
	   String [] setArr = set.toArray(new String[0]);
	   for(int i = 0; i < sArr.length; i++) {
	    if ( sArr[i].equals(setArr[i])){
	     //�����Ӧλ�õ�Ԫ����ͬ����positionΪ��ǰλ�õ�ֵ
	     position = i;
	    } else{
	     break;
	    }
	   }
	   //���position��������ĳ��ȣ�˵���Ѿ��ҵ�ĳ��setList�еļ�����
	   //set������ͬ�ˣ��˳�ѭ�������ذ���
	   //���򣬰�position��Ϊ0������һ���Ƚ�
	   if ( position == sArr.length - 1) {
	    flag = true;
	    break;
	   } else {
	    flag = false;
	    position = 0;
	   }
	  }
	  return flag;
	 }
	 
	 /**
	  * ���k�������k-1���Ӽ�
	  * 
	  * @param set
	  * @return
	  */
	 List<Set<String>> getSubset(Set <String> set){
	  
	  List<Set<String>> result = new ArrayList<Set<String>>();
	  String [] setArr = set.toArray(new String[0]);
	  
	  for( int i = 0; i < setArr.length; i++){
	   Set<String> subSet = new TreeSet<String>();
	   for(int j = 0; j < setArr.length; j++){
	    if( i != j){
	     subSet.add((String) setArr[j]);
	    }
	   }
	   result.add(subSet);
	  }
	  return result;
	 }
	 /**
	  * ���ܣ���ӡƵ���
	  */
	 void printSet(List<Set<String>> setList, int i){
	  System.out.print("Ƶ��" + i + "��� ��" + setList.size() + "� {");
	  for(Set<String> set : setList) {
	   System.out.print("[");
	   for(String str : set) {
	    System.out.print(str + " ");
	   }
	   System.out.print("], ");
	  }
	  System.out.println("}");
	 }
}
