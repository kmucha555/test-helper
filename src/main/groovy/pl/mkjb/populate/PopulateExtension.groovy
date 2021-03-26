package pl.mkjb.populate

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

class PopulateExtension implements IAnnotationDrivenExtension<Populate> {

    @Override
    void visitSpecAnnotation(final Populate annotation, final SpecInfo spec) {
        def testProperty = new TestProperty(annotation.db(), annotation.coll(), annotation.from())
        def listener = new ErrorListener()
        def interceptor = new PopulateInterceptor(testProperty)

        spec.addSetupSpecInterceptor(interceptor)
        spec.addListener(listener)
    }

    @Override
    void visitFeatureAnnotation(final Populate annotation, final FeatureInfo feature) {
        def testProperty = new TestProperty(annotation.db(), annotation.coll(), annotation.from())
        def interceptor = new PopulateInterceptor(testProperty)

        feature.addInterceptor(interceptor)
    }

    def class TestProperty {
        String dbName
        String[] dbColl
        String[] populateFrom

        TestProperty(String dbName, String[] dbColl, String[] populateFrom) {
            if (dbColl.size() != populateFrom.size()) {
                throw new IllegalArgumentException("Number of data source files and number of db collections not match.")
            }

            this.dbName = dbName
            this.dbColl = dbColl
            this.populateFrom = populateFrom
        }
    }

    def class ErrorListener extends AbstractRunListener {
        List<ErrorInfo> errors = []

        @Override
        void error(ErrorInfo error) {
            errors.add(error)
        }
    }
}
