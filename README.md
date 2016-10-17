# raposa-tucano
This is the source code of the projects Raposa and Tucano, two tools joint together in a framework, described in the to-be-published article "A Framework to Collect and Extract Publication Lists of a Given Researcher from the Web".

## prerequisites
To run the code you will need Java JDK to compile and execute. The code was compiled without problems with Java version "1.8.0_40", therefore any version more recent will work. All libraries used by the code are already included in the code or in the Libraries folder. The Libraries folder contains JSOUP version "1.7.3", used to parse the html pages. The code is indenpendent of the operational system, hence both Linux and Windows machines can run it. You will also need an id and key to Google's Custom Search API (https://cse.google.com/cse/).

## how to use
The tool receives two input files, one in XML format and the other as a text file. The XML file contains researchers and four of theirs publications, which are used to search for pages with publications lists of each one. The text file must contains two lines corresponding to information used in the search API. Fhe first one with the key and the second with the id of Google Custom Search API ([https://developers.google.com/custom-search/](https://developers.google.com/custom-search/)), necessary in the tool to realize the searches. Below is an example of how both files should be formatted:

XML input file:

    <Pesquisadores>
    
      <Pesquisador>
        <Nome> Nivio Ziviani </Nome>
        <Publicacoes>
          <Pub> Exploratory and Interactive Daily Deals Recommendation </Pub>
          <Pub> Using Mutual Influence to Improve Recommendations </Pub>
          <Pub> A New Approach for Verifying URL Uniqueness in Web Crawlers </Pub>
          <Pub> The Evolution of Web Content and Search Engines </Pub>    
        </Publicacoes>
      </Pesquisador>
    
      <Pesquisador>
        <Nome> David M. Blei </Nome>
        <Publicacoes>
          <Pub> Efficient online inference for Bayesian nonparametric relational models  </Pub>
          <Pub> Efficient discovery of overlapping communities in massive networks  </Pub>
          <Pub> Variational inference in nonconjugate models </Pub>
          <Pub> Truncation-free stochastic variational inference for Bayesian nonparametric models </Pub>
        </Publicacoes>
      </Pesquisador>
    
    </Pesquisadores>

Google Custom Search API info:

HIzcSyC7iDOdUDDC8bbcTLzz2VMVwBW9sbzQcS3
327923967239659376375:f5gkzbcm2sk

In the folder Arquivos\Entrada there are two files as example.

## creating your own dataset
Currently, Raposa and Tucano work together properly for Computer Science field as our database (inverted-index) was created using Bibtex files and XML files (in this case, from DBLP ([http://dblp.uni-trier.de/xml/](http://dblp.uni-trier.de/xml/))). 

To make the project really useful also for other fields, such as Biology, it is highly recommended the addition of new sources of citations from Biology field. It is possible by creating a file called *citation-dataset.bib* containing a number of Bibtex-formatted citations and replace the already existent citation-dataset.bib, in the folder Arquivos\Extrator.

Other form to do this is by using XML files. It is not obvious, but it is possible to use it by following the example, in the source-code (src\NovoExtrator\html\ExtractorHTML.java), line 79. If the XML file is similar to:

    <dblp>
      <article key="journals/acta/BayerM72" mdate="2003-11-25">
      <author>Rudolf Bayer</author>
      <author>Edward M. McCreight</author>
      <title>Organization and Maintenance
      of Large Ordered Indices</title>
      </article>
    </dblp>

with a few changes you can use it. Otherwise, you can develop your own method to deal with your specific XML. The methods related to obtaining data from Bibtex and XML files are in the file *src\NovoExtrator\filehandlers\FileHandler.java*.
  