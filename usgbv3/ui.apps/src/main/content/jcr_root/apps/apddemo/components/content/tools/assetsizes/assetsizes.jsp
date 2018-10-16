<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="javax.jcr.*,
                 com.day.cq.commons.Externalizer,
                 java.util.HashMap,
                 java.util.Iterator,
                 java.io.IOException,
                 com.adobe.granite.xss.XSSAPI,
                 org.apache.commons.lang3.StringEscapeUtils" %>

<html>
<%
    Externalizer externalizer = sling.getService(Externalizer.class);
	String currentPagePath = "/adminstuff/assetsize";
    String rootpath = request.getParameter("path");

    if (rootpath == null || rootpath.equals("") || rootpath.equals("/")) {
        rootpath = "/content/dam/demo";
%>
<script>rootpath = "/content/dam/demo";</script>
<%
} else {
%>
<script>rootpath = "<%= StringEscapeUtils.escapeEcmaScript(rootpath) %>";</script>
<%
    }

%>
<title>
</title>
<body>

<style>
    .number {
        text-align: right;
        height: 40px;
    }

    .total {
        font-weight: 800;
    }

    .bar {
        height: 24px;
        padding: 0;
        border: solid 1px #c0c0c0;
        background: #fff url(/libs/cq/reporting/components/diskusage/bar.gif) repeat-x;
    }
</style>
<script>

    function compareAccounts(a, b) {
        if (a.size > b.size) return (-1)
        else return (1);
    }
    function toHuman(num) {
        var k = 1024;
        var m = 1024 * k;
        var g = 1024 * m;

        var hsize = num;
        if (hsize > g) {
            hsize = Math.round(hsize / g * 100) / 100 + "GB";
        } else if (hsize > 100 * m) {
            hsize = Math.round(hsize / m) + "MB";
        } else if (hsize > 10 * m) {
            hsize = Math.round(hsize / m * 10) / 10 + "MB";
        } else if (hsize > m) {
            hsize = Math.round(hsize / m * 100) / 100 + "MB";
        } else if (hsize > 10 * k) {
            hsize = Math.round(hsize / k) + "KB";
        }
        return hsize;

    }

    function updateUI(data) {
        data = data.sort(compareAccounts);
        var elem = document.getElementById("restable");
        var res = "<table><tr><th>account</th><th>size</th><th>nodes</th><th>props</th><th></th></tr>";
        var totalsize = 0;
        var totalprops = 0;
        var totalnodes = 0;
        for (var a in data) {
            var row = data[a];
            totalsize += row.size;
            totalprops += row.props;
            totalnodes += row.nodes;
        }

        for (var a in data) {
            var row = data[a];
            var percent = Math.round(row.size * 100 / totalsize);
            var hsize = toHuman(row.size);
            var accountDom = document.createElement("a");
            accountDom.textContent = row.name;
            accountDom.setAttribute("href", "<%= externalizer.relativeLink(slingRequest, currentPagePath) %>.html?path=" + rootpath + "/" + row.name);
            res += "<tr><td>" + accountDom.outerHTML + "</td><td class=\"number\">" + hsize + "</td><td class=\"number\">" + row.nodes + "</td><td class=\"number\">" + row.props + "</td><td width=\"100%\"><table style=\"border: none;\" width=\"100%\"><td style=\"border-width:" + (percent > 0 ? 1 : 0) + ";width:" + percent + "%\" class=\"bar\"></td><td style=\"border: none; width:" + (100 - percent) + "%\"><b>" + percent + "%</b></td></table></td></tr>";
        }
        var hsize = toHuman(totalsize);
        res += "<tr><td class=\"total\">Total</td><td class=\"total number\">" + hsize + "</td><td class=\"total number\">" + totalnodes + "</td><td class=\"total number\">" + totalprops + "</td><td></td></tr>";
        res += "</table>";
        elem.innerHTML = res;

    }
</script>
<script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
<p>
    <br/><h1> Path : <%= rootpath %></h1>
</p>

<%!
    public void dumpInfo(HashMap accounts, JspWriter out, XSSAPI xssAPI) throws IOException {
        Iterator keys = accounts.keySet().iterator();

        out.write("<script>updateUI([");

        long totalsize = 0;
        long totalprops = 0;
        long totalnodes = 0;
        String comma = "";

        while (keys.hasNext()) {
            String a = (String) keys.next();
            HashMap counter = (HashMap) accounts.get(a);
            long size = (Long) counter.get("size");
            long nodes = (Long) counter.get("nodes");
            long props = (Long) counter.get("props");
            totalsize += size;
            totalprops += props;
            totalnodes += nodes;
            out.write(comma + "{\"name\":\"" + xssAPI.encodeForJSString(a) + "\",\"size\":" + size + ", \"nodes\":" + nodes + ", \"props\":" + props + "}");
            comma = ",";
        }

        out.write("])</script>");
        out.flush();
    }
%>
<%!
    public void traverse(Node parent, HashMap accounts, String accountname, JspWriter out, XSSAPI xssAPI) throws Exception {


        if (accountname != null) {
            if (!accounts.containsKey(accountname)) {
                HashMap counter = new HashMap();
                counter.put("size", 0L);
                counter.put("nodes", 0L);
                counter.put("props", 0L);
                accounts.put(accountname, counter);
            }
        }

        NodeIterator nodes = parent.getNodes();

        while (nodes.hasNext()) {
            Node n = nodes.nextNode();

            String actn = accountname;
            if (actn == null) actn = n.getName();
            traverse(n, accounts, actn, out, xssAPI);
            if (accountname != null) {
                HashMap counter = (HashMap) accounts.get(accountname);
                long nodecount = ((Long) counter.get("nodes")) + 1;
                if (nodecount % 1000 == 0) {
                    dumpInfo(accounts, out, xssAPI);
                }

                counter.put("nodes", nodecount);
            }
        }

        PropertyIterator props = parent.getProperties();

        while (props.hasNext()) {
            Property p = props.nextProperty();
            if (accountname != null) {
                HashMap counter = (HashMap) accounts.get(accountname);
                counter.put("props", ((Long) counter.get("props")) + 1);
                if (p.getDefinition().isMultiple()) {
                    long[] lenghts = p.getLengths();
                    long total = 0;
                    for (long len : lenghts) {
                        total += len;
                    }
                    counter.put("size", ((Long) counter.get("size")) + total);
                } else {
                    counter.put("size", ((Long) counter.get("size")) + p.getLength());
                }
            }
        }
    }

%>
<div id="restable">

</div>

<%

    Node homes = resourceResolver.getResource(rootpath).adaptTo(Node.class);
//Node homes = (Node) currentNode.getSession().getItem(rootpath);

    HashMap accounts = new HashMap();
    traverse(homes, accounts, null, out, xssAPI);

    dumpInfo(accounts, out, xssAPI);

%>
</body>
</html>

