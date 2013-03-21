(: XQuery main module :)

let $document := <root>
<request>
    <name>anatol</name>
    <pass>12443</pass>
    <action>create</action>
</request>
</root>
return
<response>
{
    let $action := $document/request/action
    return if($action eq 'submit')
    then
        <_next>
            <node>SUBMIT</node>
        </_next>
    else
        <_next>
            <node>CREATE</node>
        </_next>
}

<name action="keep">{$document/request/name}</name>
<name action="keep">{$document/request/pass}</name>
</response>
