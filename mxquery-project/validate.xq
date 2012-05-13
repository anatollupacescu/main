(: XQuery main module :)

let $document := <root><request><name>anatol</name><pass>12443</pass></request><error>Please enter username and passwor</error></root>
return
<response>
{
    let $valid := (string-length($document/request/name) gt 4 and string-length($document/request/pass) gt 4)
    return if($valid eq true())
    then
        <_next>
            <node>CHECK</node>
        </_next>
    else
        <_next>
            <param xpath='//error'>error</param>
            <page>user/form</page>
        </_next>
}
<user action="retrieve" cols="name">
    <name operation="EQ">{$document/request/name}</name>
    <pass operation="EQ">{$document/request/pass}</pass>
</user>

</response>
