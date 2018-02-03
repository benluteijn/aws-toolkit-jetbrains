package software.aws.toolkits.jetbrains.s3.explorer


import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.Bucket
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import software.aws.toolkits.jetbrains.aws.s3.S3BucketVirtualFile
import software.aws.toolkits.jetbrains.aws.s3.S3VirtualFileSystem
import software.aws.toolkits.jetbrains.core.AwsClientManager
import software.aws.toolkits.jetbrains.ui.S3_BUCKET_ICON
import software.aws.toolkits.jetbrains.ui.S3_SERVICE_ICON
import software.aws.toolkits.jetbrains.ui.explorer.AwsExplorerNode
import software.aws.toolkits.jetbrains.ui.explorer.AwsExplorerResourceNode
import software.aws.toolkits.jetbrains.ui.explorer.AwsExplorerServiceRootNode
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class AwsExplorerS3RootNode(project: Project) :
        AwsExplorerServiceRootNode(project, "Amazon S3", S3_SERVICE_ICON) {
    override fun serviceName() = "s3"; // TODO: Get from client in v2

    private val client: AmazonS3 = AwsClientManager.getInstance(project).getClient()

    override fun loadResources(paginationToken: String?): Collection<AwsExplorerNode<*>> {
        return client.listBuckets().map { mapResourceToNode(it) }
    }

    private fun mapResourceToNode(resource: Bucket) = AwsExplorerBucketNode(project!!, this, resource)
}

class AwsExplorerBucketNode(project: Project, serviceNode: AwsExplorerS3RootNode, private val bucket: Bucket) :
        AwsExplorerResourceNode<Bucket>(project, serviceNode, bucket, S3_BUCKET_ICON) {
    private val editorManager = FileEditorManager.getInstance(project)
    private val client: AmazonS3 = AwsClientManager.getInstance(project).getClient()

    override fun getChildren(): Collection<AbstractTreeNode<Any>> = emptyList()
    override fun resourceName(): String = "bucket"

    override fun onDoubleClick(model: DefaultTreeModel, selectedElement: DefaultMutableTreeNode) {
        val bucketVirtualFile = S3BucketVirtualFile(S3VirtualFileSystem(client), bucket)
        editorManager.openFile(bucketVirtualFile, true)
    }

    override fun toString(): String = bucket.name
}