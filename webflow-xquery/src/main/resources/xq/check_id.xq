(: XQuery main module :)

let $document := <root><request><user key="3"><name>vasea</name></user><error>No such user</error></request></root>
return
<response>
{
    let $x := count($document/request/user)
    return if ($x gt 0)
    then 
        <_next>
            <item xpath="//user">currentUser</item>
            <page>user/form</page>
        </_next>
    else
        <_next>
            <item xpath="//error">error</item>
            <page>user/form</page>
        </_next>
}
</response>