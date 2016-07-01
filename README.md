

## xml-tools

매우 큰 XML을 처리할 때 사용할 수 있는 도구입니다.
javax.xml.stream 패키지를 사용하며 XML 크기에 제한이 없습니다.
소스는 정리되지 않았습니다. 급하실때 쓰세요.

JDK1.6 이상에서 실행하였으며, 테스트를 제외하고 의존 라이브러리가 없습니다.

현재 세가지 기능이 구현되어 있습니다.

* tree : XML의 전체적인 구조와 엘리먼트의 갯수 그리고 샘플 텍스트를 보여줍니다.
* head : 지정된 엘리먼트 N개를 추출합니다.
* extract : 지정된 엘리먼트의 텍스트 요소를 추출합니다.

---

아래에서 XML 샘플은 http://www.cs.washington.edu/research/xmldatasets/www/repository.html#mondial 에서 lineitem.xml(32.3MB) 를 사용하였습니다.
 
### tree
XML의 전체적인 구조를 파악할 때 유용할 것입니다.

    $ java -jar target/xml-tools-0.0.1-SNAPSHOT.jar tree lineitem.xml

    table	1
      T	60175	
        L_ORDERKEY	60175	60000
        L_PARTKEY	60175	836
        L_SUPPKEY	60175	3
         ...
        L_RECEIPTDATE	60175	1995-07-24
        L_SHIPINSTRUCT	60175	DELIVER IN PERSON
        L_SHIPMODE	60175	TRUCK
        L_COMMENT	60175	carefully final packages beyon


결과에서 첫번째 필드는 엘리먼트의 이름이고 그 다음으로 엘리먼트의 갯수, 그리고 마지막으로 나타난 텍스트입니다. 탭으로 구분됩니다.
lineitem.xml 에는 /table/T 엘리먼트가 60,175 개가 있습니다.

### head
head 는 XML 구조를 유지하면서 지정된 엘리먼트와 하위 엘리먼트를 반복적으로 추출하는 기능입니다. -NN으로 건수를 지정합니다.
 
    $ java -jar target/xml-tools-0.0.1-SNAPSHOT.jar head -3 /table/T lineitem.xml

    <?xml version="1.0" encoding="UTF-8"?><table ID="lineitem"><T><L_ORDERKEY>1</L_ORDERKEY>...</T>
    <T><L_ORDERKEY>1</L_ORDERKEY>...</T>
    <T><L_ORDERKEY>1</L_ORDERKEY>...</T>
    </table>

### extract
extract 는 텍스트 내용을 추출합니다.

    $ java -jar target/xml-tools-0.0.1-SNAPSHOT.jar extract /table/T/L_SHIPMODE lineitem.xml | head -5
    2:	/table/T/L_SHIPMODE	TRUCK
    3:	/table/T/L_SHIPMODE	MAIL
    4:	/table/T/L_SHIPMODE	REG AIR
    5:	/table/T/L_SHIPMODE	AIR
    6:	/table/T/L_SHIPMODE	FOB

결과에서 첫번째 컬럼은 라인번호, 매치한 엘리먼트, 그 다음으로 엘리먼트의 텍스트 내용입니다.

