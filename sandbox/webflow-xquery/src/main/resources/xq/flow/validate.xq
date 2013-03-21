(: XQuery main module :)

let $document := <root>
<request xml:base="">
    <name>anatol</name>
    <pass>12345</pass>
</request>
</root>
return
<request>
    
    <name>{data($document/request/name)}</name>
    <pass>{data($document/request/pass)}</pass>
    
    <_next>
        <stringValue name="name">{data($document/request/name)}</stringValue>
    
    {
        let $has-errors := string-length($document/request/pass) < 4 or string-length($document/request/name) < 4
        return if($has-errors eq true())
        then <code>yes</code>
        else
            <code>no</code>
    }
    
    {
        let $name := string-length($document/request/name)
        return if($name gt 4)
        then ''
        else
            <stringValue name="errorname">Username is not valid</stringValue>
    }
    
    {
        let $pass := string-length($document/request/pass)
        return if($pass gt 4)
        then ''
        else
             <stringValue name="errorpass">Password is not valid</stringValue>
    }
    </_next>
    
</request>
