# Libraries directory

Must contain SIU libraries:

* siuloader.jar
* siuutils.jar

You should create a specific directory structure in project lib:

mkdir -p ${project.basedir}/lib/com/hp/siu/loader/${sui.version}
mkdir -p ${project.basedir}/lib/com/hp/siu/util/${sui.version}

And place libraries named loader-${sui.version}.jar and
util-${sui.version}.jar into appropriate directories.
