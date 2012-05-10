<request>

<user action="retrieve" columns="name,age">
    <name condition="EQ">{data($document/request/name)}</name>
    <pass condition="EQ">{data($document/request/pass)}</pass>
</user>

<login_attempt action="retrieve" columns="count" key="{data($document/request/name)}" />

</request>
