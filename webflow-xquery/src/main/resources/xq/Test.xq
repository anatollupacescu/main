(: XQuery main module :)

let $document := <root><request><userid>3</userid></request></root>
return
<response>
    {
        let $userid := $document/request/userid
        return if(string-length($userid) gt 0)
        then 
            <user action="retrieve" key="{data($userid)}" columns="name,age" test="{string-length($userid)}"/>
        else ''
    }

    <_next>
        <node>CHECK</node>
    </_next>
</response>