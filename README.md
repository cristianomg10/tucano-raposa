# raposa-tucano
This is the source code of the projects Raposa and Tucano, two tools joint together in a framework, described in the to-be-published article "A Framework to Collect and Extract Publication Lists of a Given Researcher from the Web".

## how to use
The tool receives two input files, one in XML format and the other a text file. The XML file contains researchers and four of theirs publications, which are used to search for pages with publications lists of each one. The text file must contains two lines corresponding to information used in the search API. Fhe first one with the key and the second with the id of Google Custom Search API (https://developers.google.com/custom-search/), necessary in the tool to realize the searches. Below is an example of how both files should be formatted:

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

In the folder XXXXXXX there are two files as example.

## Functioning

The tool receives two input files, one with researchers and publications and the other with the API info. For each researcher provided, the tool will generate queries based on the four publications given as input, every query will be searched in the web through Google Custom Search, the first 10 results are retrieved and stored for further processing. In the code at the API call more results can requested. After the search each page is loaded together with its title, snippet, link and content. This information will be used by Tucano to inspect if it is a page with publications and information about the author. Inside Tucano functioning a call is made to Raposa to extract citations contained in the page, if it contains any. Raposa will load its data structures and files and analyse the page, the citations found in the page will be returned to Tucano for further evaluation. After receiving the citations, Tucano will continue its examination of the page, together with its info of the page (title, snippet, link and content). Using the aforementioned information about the page, Tucano will decide if the page is a publication lists page of the author or not.

## authors
This work had been developed at **LICESA** (Lab. of Computational Intelligence and Autonomous Systems), located at **DCC** (Department of Computer Science), in **UFLA** (Federal University of Lavras / Brazil), and is authored by:

 - Prof. **Denilson Alves Pereira**, PhD. (denilsonpereira[at]dcc.ufla.br)  
 - **Armando Honório** (armdhp[at]gmail.com) - BSc. in Computer Science
 - **Cristiano M. Garcia** (cristiano.garcia[at]dgti.ufla.br) - BSc. in Information Systems
