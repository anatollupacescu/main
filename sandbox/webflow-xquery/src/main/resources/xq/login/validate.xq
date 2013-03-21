(: XQuery main module :)

let $document := <request><errors><error-user>Username is not valid</error-user><error-pass>Password is not valid</error-pass></errors></request>
return
<request>
    
    {
        let $he := count($document/errors/*)
        return $he
    }

</request>
