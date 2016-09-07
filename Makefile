FILES=file1.py file1.java file_with.special-chars.js subdir/file1.java

test-files:
	rm -rf test/awesome/integration
	mkdir -p test/awesome/integration{,/subdir}
	for i in `seq 100001 100051`; do echo "//$$i" >> test/awesome/integration/testfile; done
	for F in $(FILES); do cp test/awesome/integration/{testfile,$$F}; done
