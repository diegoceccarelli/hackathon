<?php
header('Content-Type: text/xml');
$url = 'http://api.europeana.eu/api/opensearch.json?wskey=IAVQBBDOQQ&searchTerms=hugo';
$content = file_get_contents($url);
echo $content;
?>